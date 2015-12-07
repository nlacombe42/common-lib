package net.maatvirtue.commonlib.exception;

public class InvalidTagFfpdpException extends FfpdpException
{
	public InvalidTagFfpdpException()
	{
		//Do nothing
	}

	public InvalidTagFfpdpException(String message)
	{
		super(message);
	}

	public InvalidTagFfpdpException(Throwable cause)
	{
		super(cause);
	}

	public InvalidTagFfpdpException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
