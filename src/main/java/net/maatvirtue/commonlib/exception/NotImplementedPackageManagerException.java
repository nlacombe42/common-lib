package net.maatvirtue.commonlib.exception;

public class NotImplementedPackageManagerException extends PackageManagerException
{
	public NotImplementedPackageManagerException()
	{
		//Do nothing
	}

	public NotImplementedPackageManagerException(String message)
	{
		super(message);
	}

	public NotImplementedPackageManagerException(Throwable cause)
	{
		super(cause);
	}

	public NotImplementedPackageManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
