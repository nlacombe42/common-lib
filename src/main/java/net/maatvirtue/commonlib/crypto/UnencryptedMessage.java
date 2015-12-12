package net.maatvirtue.commonlib.crypto;

import org.apache.commons.codec.binary.Hex;

import java.security.PublicKey;

public class UnencryptedMessage
{
	private SignedMessage signedMessage;
	private byte[] notSignedMessage;

	public UnencryptedMessage(SignedMessage signedMessage)
	{
		this.signedMessage = signedMessage;
	}

	public UnencryptedMessage(byte[] notSignedMessage)
	{
		this.notSignedMessage = notSignedMessage;
	}

	public boolean isSigned()
	{
		return signedMessage != null;
	}

	public byte[] getMessage()
	{
		if(isSigned())
			return signedMessage.getMessage();
		else
			return notSignedMessage;
	}

	public boolean isSignatureValid()
	{
		return isSigned() && signedMessage.isSignatureValid();
	}

	public PublicKey getSignerPublicKey()
	{
		if(isSigned())
			return signedMessage.getSignerPublicKey();
		else
			return null;
	}

	@Override
	public String toString()
	{
		String ret = "[UnencryptedMessage| ";

		if(!isSigned())
			ret += "message: "+new String(Hex.encodeHex(getMessage()))+" ";
		else
		{
			ret += "isSignatureValid: "+isSignatureValid()+", ";
			ret += "signerPublicKey: "+new String(Hex.encodeHex(getSignerPublicKey().getEncoded()))+", ";
			ret += "message: "+new String(Hex.encodeHex(getMessage()))+" ";
		}

		ret += "]";

		return ret;
	}
}
