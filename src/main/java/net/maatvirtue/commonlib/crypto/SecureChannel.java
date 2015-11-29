package net.maatvirtue.commonlib.crypto;

import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.HostNotTrustedException;
import net.maatvirtue.commonlib.net.FrameInputStream;
import net.maatvirtue.commonlib.net.FrameOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;

public class SecureChannel
{
	private KeyPair localKeypair;
	private FrameInputStream fis;
	private FrameOutputStream fos;
	private byte[] localAesKey;
	private byte[] remoteAesKey;
	private CryptoUtil cryptoUtil;
	private HostTrustValidator hostTrustValidator;

	public SecureChannel(InputStream is, OutputStream os, KeyPair keypair, HostTrustValidator hostTrustValidator) throws IOException, CryptoException, HostNotTrustedException
	{
		cryptoUtil = CryptoUtil.getInstance();

		this.fis = new FrameInputStream(is);
		this.fos = new FrameOutputStream(os);
		this.localKeypair = keypair;
		this.hostTrustValidator = hostTrustValidator;

		initializeCommunication();
	}

	private void initializeCommunication() throws IOException, CryptoException, HostNotTrustedException
	{
		fos.writeFrame(cryptoUtil.serializePublicKey(localKeypair.getPublic()));

		PublicKey remotePublicKey = cryptoUtil.deserializePublicKey(fis.readFrame());

		if(!hostTrustValidator.isTrustedHost(remotePublicKey))
			throw new HostNotTrustedException(remotePublicKey);

		localAesKey = cryptoUtil.generateRandomAesKey();

		fos.writeFrame(cryptoUtil.encryptRsa(remotePublicKey, localAesKey));

		remoteAesKey = cryptoUtil.decryptRsa(localKeypair.getPrivate(), fis.readFrame());
	}

	public void sendMessage(byte[] message) throws IOException, CryptoException
	{
		byte[] iv = cryptoUtil.generateRandomAesIv();

		fos.writeFrame(iv);
		fos.writeFrame(cryptoUtil.encryptAes(localAesKey, iv, message));
	}

	public byte[] readMessage() throws IOException, CryptoException
	{
		byte[] iv = fis.readFrame();
		byte[] encryptedMessage = fis.readFrame();

		return cryptoUtil.decryptAes(remoteAesKey, iv, encryptedMessage);
	}
}
