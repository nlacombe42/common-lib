package net.maatvirtue.commonlib.domain.crypto;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

public class SignedMessage
{
	private byte[] message;
	private PublicKey signerPublicKey;

	public SignedMessage(byte[] message, PublicKey signerPublicKey)
	{
		this.message = message;
		this.signerPublicKey = signerPublicKey;
	}

	public boolean isSignatureValid()
	{
		return signerPublicKey != null;
	}

	public byte[] getMessage()
	{
		return message;
	}

	public PublicKey getSignerPublicKey()
	{
		return signerPublicKey;
	}

	@Override
	public String toString()
	{
		String ret = "[UnencryptedMessage| ";

		ret += "isSignatureValid: "+isSignatureValid()+", ";
		ret += "signerPublicKey: "+new String(Hex.encodeHex(getSignerPublicKey().getEncoded()))+", ";
		ret += "message: "+new String(getMessage())+" ";

		ret += "]";

		return ret;
	}
}
