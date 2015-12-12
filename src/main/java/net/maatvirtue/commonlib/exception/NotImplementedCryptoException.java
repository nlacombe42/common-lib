package net.maatvirtue.commonlib.exception;

public class NotImplementedCryptoException extends CryptoException
{
	public NotImplementedCryptoException()
	{
		//Do nothing
	}

	public NotImplementedCryptoException(String message)
	{
		super(message);
	}

	public NotImplementedCryptoException(Throwable cause)
	{
		super(cause);
	}

	public NotImplementedCryptoException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
