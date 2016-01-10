package net.maatvirtue.commonlib.service.crypto;

import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.HostNotTrustedException;
import net.maatvirtue.commonlib.util.io.FrameInputStream;
import net.maatvirtue.commonlib.util.io.FrameOutputStream;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.PublicKey;

public class SecureChannel
{
	@Inject
	private CryptoService cryptoService;

	private KeyPair localKeypair;
	private FrameInputStream fis;
	private FrameOutputStream fos;
	private byte[] localAesKey;
	private byte[] remoteAesKey;
	private HostTrustValidator hostTrustValidator;

	public SecureChannel(InputStream is, OutputStream os, KeyPair keypair, HostTrustValidator hostTrustValidator) throws IOException, CryptoException, HostNotTrustedException
	{
		this.fis = new FrameInputStream(is);
		this.fos = new FrameOutputStream(os);
		this.localKeypair = keypair;
		this.hostTrustValidator = hostTrustValidator;

		initializeCommunication();
	}

	private void initializeCommunication() throws IOException, CryptoException, HostNotTrustedException
	{
		fos.writeFrame(cryptoService.serializePublicKey(localKeypair.getPublic()));

		PublicKey remotePublicKey = cryptoService.deserializePublicKey(fis.readFrame());

		if(!hostTrustValidator.isTrustedHost(remotePublicKey))
			throw new HostNotTrustedException(remotePublicKey);

		localAesKey = cryptoService.generateRandomAesKey();

		fos.writeFrame(cryptoService.encryptRsa(remotePublicKey, localAesKey));

		remoteAesKey = cryptoService.decryptRsa(localKeypair.getPrivate(), fis.readFrame());
	}

	public void sendMessage(byte[] message) throws IOException, CryptoException
	{
		byte[] iv = cryptoService.generateRandomAesIv();

		fos.writeFrame(iv);
		fos.writeFrame(cryptoService.encryptAes(localAesKey, iv, message));
	}

	public byte[] readMessage() throws IOException, CryptoException
	{
		byte[] iv = fis.readFrame();
		byte[] encryptedMessage = fis.readFrame();

		return cryptoService.decryptAes(remoteAesKey, iv, encryptedMessage);
	}
}
