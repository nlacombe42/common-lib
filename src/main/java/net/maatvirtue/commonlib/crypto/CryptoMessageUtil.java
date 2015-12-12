package net.maatvirtue.commonlib.crypto;

import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.NotImplementedCryptoException;
import net.maatvirtue.commonlib.ffpdp.FfpdpTag;
import net.maatvirtue.commonlib.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.ffpdp.FfpdpUtil;
import net.maatvirtue.commonlib.ffpdp.FfpdpVersion;
import net.maatvirtue.commonlib.io.FrameInputStream;
import net.maatvirtue.commonlib.io.FrameOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class CryptoMessageUtil implements Serializable
{
	private static final int FFPDP_CRYPTO_UID = 1;
	private static final int FFPDP_SIGNED_MESSAGE_TYPE = 1;
	private static final int FFPDP_ENCRYPTED_MESSAGE_TYPE = 2;
	private static final FfpdpTagV2 currentSignedMessageTag = new FfpdpTagV2(FFPDP_CRYPTO_UID, FFPDP_SIGNED_MESSAGE_TYPE, 1, 0);
	private static final FfpdpTagV2 currentEncryptedMessageTag = new FfpdpTagV2(FFPDP_CRYPTO_UID, FFPDP_ENCRYPTED_MESSAGE_TYPE, 1, 0);

	public static byte[] signAndEncryptMessage(KeyPair signingKeypair, PublicKey encryptionKey, byte[] message) throws IOException, CryptoException
	{
		return encryptMessage(encryptionKey, signMessage(signingKeypair, message));
	}

	public static byte[] signMessage(KeyPair signingKeypair, byte[] message) throws IOException, CryptoException
	{
		if(signingKeypair==null || message==null)
			throw new IllegalArgumentException("signingKeypair and message must not be null");

		CryptoUtil cryptoUtil = CryptoUtil.getInstance();
		FfpdpUtil ffpdpUtil = FfpdpUtil.getInstance();

		try
		(
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FrameOutputStream fos = new FrameOutputStream(baos);
		)
		{
			byte[] signature = cryptoUtil.signSha1Rsa(signingKeypair.getPrivate(), message);

			ffpdpUtil.writeFfpdpTag(baos, currentSignedMessageTag);
			fos.writeFrame(cryptoUtil.serializePublicKey(signingKeypair.getPublic()));
			fos.writeFrame(signature);
			fos.writeFrame(message);

			return baos.toByteArray();
		}
		catch(FfpdpException exception)
		{
			throw new IOException(exception);
		}
	}

	public static SignedMessage readSignedMessage(byte[] signedMessageBytes) throws IOException, CryptoException
	{
		if(signedMessageBytes==null)
			throw new IllegalArgumentException("signedMessageBytes must not be null");

		CryptoUtil cryptoUtil = CryptoUtil.getInstance();

		try
		(
			ByteArrayInputStream bais = new ByteArrayInputStream(signedMessageBytes);
			FrameInputStream fis = new FrameInputStream(bais);
		)
		{
			FfpdpTagV2 ffpdpTagV2 = readFfpdpTagV2(bais);

			if(ffpdpTagV2.getUid()!=FFPDP_CRYPTO_UID || ffpdpTagV2.getType()!=FFPDP_SIGNED_MESSAGE_TYPE)
				throw new CryptoException("signedMessageBytes is not a signed message");

			if(ffpdpTagV2.getMajorVersion()!=currentSignedMessageTag.getMajorVersion())
				throw new NotImplementedCryptoException("signed message major version "+ffpdpTagV2.getMajorVersion()+" not implemented");

			PublicKey signerPublicKey = cryptoUtil.deserializePublicKey(fis.readFrame());
			byte[] signature = fis.readFrame();
			byte[] message = fis.readFrame();

			boolean isSignatureValid = cryptoUtil.verifySha1Rsa(signerPublicKey, message, signature);

			if(!isSignatureValid)
				signerPublicKey = null;

			return new SignedMessage(message, signerPublicKey);
		}
	}

	public static byte[] encryptMessage(PublicKey encryptionKey, byte[] message) throws IOException, CryptoException
	{
		CryptoUtil cryptoUtil = CryptoUtil.getInstance();
		FfpdpUtil ffpdpUtil = FfpdpUtil.getInstance();

		byte[] aesKey = cryptoUtil.generateRandomAesKey();
		byte[] iv = cryptoUtil.generateRandomAesIv();

		try
		(
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FrameOutputStream fos = new FrameOutputStream(baos);
		)
		{
			ffpdpUtil.writeFfpdpTag(baos, currentEncryptedMessageTag);
			fos.writeFrame(cryptoUtil.encryptRsa(encryptionKey, aesKey));
			fos.writeFrame(iv);
			fos.writeFrame(cryptoUtil.encryptAes(aesKey, iv, message));

			return baos.toByteArray();
		}
		catch(FfpdpException exception)
		{
			throw new IOException(exception);
		}
	}

	public static UnencryptedMessage decryptMessage(PrivateKey decryptionKey, byte[] encryptedMessage) throws IOException, CryptoException
	{
		CryptoUtil cryptoUtil = CryptoUtil.getInstance();
		FfpdpUtil ffpdpUtil = FfpdpUtil.getInstance();

		try
		(
			ByteArrayInputStream bais = new ByteArrayInputStream(encryptedMessage);
			FrameInputStream fis = new FrameInputStream(bais);
		)
		{
			FfpdpTagV2 ffpdpTagV2 = readFfpdpTagV2(bais);

			if(ffpdpTagV2.getUid()!=FFPDP_CRYPTO_UID || ffpdpTagV2.getType()!=FFPDP_ENCRYPTED_MESSAGE_TYPE)
				throw new CryptoException("encryptedMessage is not an encrypted message");

			if(ffpdpTagV2.getMajorVersion()!=currentEncryptedMessageTag.getMajorVersion())
				throw new NotImplementedCryptoException("encrypted message major version "+ffpdpTagV2.getMajorVersion()+" not implemented");

			byte[] encryptedAesKey = fis.readFrame();
			byte[] iv = fis.readFrame();
			byte[] encryptedPayload = fis.readFrame();

			byte[] aesKey = cryptoUtil.decryptRsa(decryptionKey, encryptedAesKey);
			byte[] message = cryptoUtil.decryptAes(aesKey, iv, encryptedPayload);

			if(isSignedMessage(message))
				return new UnencryptedMessage(readSignedMessage(message));
			else
				return new UnencryptedMessage(message);
		}
	}

	private static boolean isSignedMessage(byte[] data) {
		try(ByteArrayInputStream bais = new ByteArrayInputStream(data))
		{
			FfpdpTagV2 ffpdpTagV2 = readFfpdpTagV2(bais);

			return ffpdpTagV2.getUid()==FFPDP_CRYPTO_UID && ffpdpTagV2.getType()==FFPDP_SIGNED_MESSAGE_TYPE &&
							ffpdpTagV2.getMajorVersion()==currentSignedMessageTag.getMajorVersion();
		}
		catch(CryptoException|IOException exception)
		{
			return false;
		}
	}

	private static FfpdpTagV2 readFfpdpTagV2(InputStream is) throws IOException, CryptoException
	{
		try
		{
			FfpdpUtil ffpdpUtil = FfpdpUtil.getInstance();

			FfpdpTag ffpdpTag = ffpdpUtil.readFfpdpTag(is);

			if(ffpdpTag.getFfpdpVersion()!=FfpdpVersion.V2)
				throw new NotImplementedCryptoException("FFPDP version "+ffpdpTag.getFfpdpVersion()+" not implemented");

			return (FfpdpTagV2)ffpdpTag;
		}
		catch(FfpdpException exception)
		{
			throw new IOException(exception);
		}
	}
}
