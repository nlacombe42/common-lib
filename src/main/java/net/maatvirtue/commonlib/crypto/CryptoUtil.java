package net.maatvirtue.commonlib.crypto;

import net.maatvirtue.commonlib.exception.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;

public class CryptoUtil
{
	private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	private static final String SHA1_RSA_TRANSFORMATION = "SHA1withRSA";
	private static final String BOUNCY_CASTLE_PROVIDER = "BC";

	private static CryptoUtil instance;

	private CryptoUtil()
	{
		loadBouncyCastleProvider();
	}

	public static CryptoUtil getInstance()
	{
		if(instance==null)
			instance = new CryptoUtil();

		return instance;
	}

	private void loadBouncyCastleProvider()
	{
		if (Security.getProvider(BOUNCY_CASTLE_PROVIDER) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	public byte[] SHA256(String text) throws CryptoException
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(text.getBytes("UTF-8"), 0, text.length());

			byte[] sha1hash = md.digest();

			return sha1hash;
		}
		catch(Exception exception)
		{
			throw new CryptoException(exception);
		}
	}

	public byte[] encryptRsa(PublicKey publicKey, byte[] message) throws CryptoException
	{
		try
		{
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			return cipher.doFinal(message);
		}
		catch(Exception exception)
		{
			throw new CryptoException(exception);
		}
	}

	public byte[] decryptRsa(PrivateKey privateKey, byte[] encryptedMessage) throws CryptoException
	{
		try
		{
			Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			return cipher.doFinal(encryptedMessage);
		}
		catch(Exception exception)
		{
			throw new CryptoException(exception);
		}
	}

	public byte[] signSha1Rsa(PrivateKey privateKey, byte[] message) throws CryptoException
	{
		try
		{
			Signature sign = Signature.getInstance(SHA1_RSA_TRANSFORMATION);
			sign.initSign(privateKey);
			sign.update(message);

			return sign.sign();
		}
		catch(Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public boolean verifySha1Rsa(PublicKey publicKey, byte[] message, byte[] signature) throws CryptoException
	{
		try
		{
			Signature sign = Signature.getInstance(SHA1_RSA_TRANSFORMATION);
			sign.initVerify(publicKey);
			sign.update(message);

			return sign.verify(signature);
		}
		catch(Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public KeyPair generateRsaKeypair() throws CryptoException
	{
		try
		{
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048, random);

			return kpg.genKeyPair();
		}
		catch(Exception exception)
		{
			throw new CryptoException(exception);
		}
	}

	public Key readKeyFromPemFile(File pemFile) throws IOException
	{
		try(PEMReader pemReader = new PEMReader(new FileReader(pemFile)))
		{
			return (Key)pemReader.readObject();
		}
	}

	public KeyPair readKeyPairFromPemFile(File pemFile) throws IOException
	{
		try(PEMReader pemReader = new PEMReader(new FileReader(pemFile)))
		{
			return (KeyPair)pemReader.readObject();
		}
	}

	public void writeKeyToPemFile(File pemFile, Key key) throws IOException
	{
		try(PEMWriter pemWriter = new PEMWriter(new FileWriter(pemFile)))
		{
			pemWriter.writeObject(key);
		}
	}
}
