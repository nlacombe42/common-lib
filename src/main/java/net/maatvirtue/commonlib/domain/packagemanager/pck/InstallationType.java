package net.maatvirtue.commonlib.domain.packagemanager.pck;

import net.maatvirtue.commonlib.service.packagemanager.packageinstaller.JarPackageHandler;
import net.maatvirtue.commonlib.service.packagemanager.packageinstaller.PackageHandler;

public enum InstallationType
{
	JAR("jar", JarPackageHandler.class)
	;

	private String code;
	private Class<? extends PackageHandler> packageHandlerClass;

	InstallationType(String code, Class<? extends PackageHandler> packageHandlerClass)
	{
		this.code = code;
		this.packageHandlerClass = packageHandlerClass;
	}

	public static InstallationType getByCode(String code)
	{
		for(InstallationType installationType : values())
			if(installationType.getCode().equals(code))
				return installationType;

		throw new IllegalArgumentException("Invalid InstallationType code: "+code);
	}

	public String getCode()
	{
		return code;
	}

	public Class<? extends PackageHandler> getPackageHandlerClass()
	{
		return packageHandlerClass;
	}
}
