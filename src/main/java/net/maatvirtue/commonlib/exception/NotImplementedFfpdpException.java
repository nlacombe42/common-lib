package net.maatvirtue.commonlib.exception;

public class NotImplementedFfpdpException extends FfpdpException
{
	public NotImplementedFfpdpException()
	{
		//Do nothing
	}

	public NotImplementedFfpdpException(String message)
	{
		super(message);
	}

	public NotImplementedFfpdpException(Throwable cause)
	{
		super(cause);
	}

	public NotImplementedFfpdpException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
