package net.maatvirtue.commonlib.exception;

public class NotInstalledPackageManagerException extends PackageManagerException
{
	public NotInstalledPackageManagerException()
	{
		//Do nothing
	}

	public NotInstalledPackageManagerException(String packageName)
	{
		super("Package not installed: "+packageName);
	}

	public NotInstalledPackageManagerException(Throwable cause)
	{
		super(cause);
	}

	public NotInstalledPackageManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
