package net.maatvirtue.commonlib.exception;

public class PackageManagerRuntimeException extends RuntimeException
{
	public PackageManagerRuntimeException()
	{
		//Do nothing
	}

	public PackageManagerRuntimeException(String message)
	{
		super(message);
	}

	public PackageManagerRuntimeException(Throwable cause)
	{
		super(cause);
	}

	public PackageManagerRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
