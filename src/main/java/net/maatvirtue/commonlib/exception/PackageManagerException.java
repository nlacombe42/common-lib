package net.maatvirtue.commonlib.exception;

public class PackageManagerException extends Exception
{
	public PackageManagerException()
	{
		//Do nothing
	}

	public PackageManagerException(String message)
	{
		super(message);
	}

	public PackageManagerException(Throwable cause)
	{
		super(cause);
	}

	public PackageManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
