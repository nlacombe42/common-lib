package net.maatvirtue.commonlib.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil
{
	public static byte[] SHA256(String text)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(text.getBytes("UTF-8"), 0, text.length());

			byte[] sha1hash = md.digest();

			return sha1hash;
		}
		catch(NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
			//Not supposed to happen
			throw new RuntimeException(e);
		}
	}
}
