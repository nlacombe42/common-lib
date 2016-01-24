package net.maatvirtue.commonlib.service.packagemanager.packageinstaller;

import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.exception.PackageManagerException;

public interface PackageInstaller
{
	void installPackage(Package pck) throws PackageManagerException;
}
