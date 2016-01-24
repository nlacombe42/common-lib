package net.maatvirtue.commonlib.service.packagemanager.packageinstaller;

import net.maatvirtue.commonlib.domain.packagemanager.pck.InstallationDataType;
import net.maatvirtue.commonlib.exception.NotImplementedPackageManagerException;

public class PackageInstallerFactory
{
	public static PackageInstaller getPackageInstaller(InstallationDataType installationDataType)
	{
		if(installationDataType==InstallationDataType.JAR)
			return new JarPackageInstaller();
		else
			throw new NotImplementedPackageManagerException();
	}
}
