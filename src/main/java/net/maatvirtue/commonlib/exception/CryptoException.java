package net.maatvirtue.commonlib.exception;

public class CryptoException extends Exception
{
	public CryptoException()
	{
		//Do nothing
	}

	public CryptoException(String message)
	{
		super(message);
	}

	public CryptoException(Throwable cause)
	{
		super(cause);
	}

	public CryptoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
