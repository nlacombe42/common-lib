package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.ApplicationSetupHandler;
import net.maatvirtue.commonlib.exception.UnkownCommandRuntimeException;

public class ApplicationSetupRunner
{
	public static void runCommand(ApplicationSetupHandler applicationSetupHandler, String command)
	{
		if(command==null)
			throw new IllegalArgumentException("command cannot be null");

		if(command.equals(PackageManagerConstants.APPLICATION_INSTALL_COMMAND))
			applicationSetupHandler.install();
		else if(command.equals(PackageManagerConstants.APPLICATION_UPGRADE_COMMAND))
			applicationSetupHandler.upgrade();
		else if(command.equals(PackageManagerConstants.APPLICATION_UNINSTALL_COMMAND))
			applicationSetupHandler.uninstall();
		else
			throw new UnkownCommandRuntimeException("unknown command: "+command);
	}
}
