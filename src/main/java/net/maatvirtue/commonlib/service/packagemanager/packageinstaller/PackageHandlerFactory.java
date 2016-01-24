package net.maatvirtue.commonlib.service.packagemanager.packageinstaller;

import net.maatvirtue.commonlib.domain.packagemanager.pck.InstallationType;
import net.maatvirtue.commonlib.exception.PackageManagerRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PackageHandlerFactory
{
	public static PackageHandler getPackageHandler(InstallationType installationType)
	{
		try
		{
			return instantiate(installationType.getPackageHandlerClass());
		}
		catch(Exception exception)
		{
			throw new PackageManagerRuntimeException("Error instantiating PackageHandler: ", exception);
		}
	}

	private static PackageHandler instantiate(Class<? extends PackageHandler> packageHandlerClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
	{
		Constructor defaultConstructor = packageHandlerClass.getConstructor();

		return (PackageHandler) defaultConstructor.newInstance();
	}
}
