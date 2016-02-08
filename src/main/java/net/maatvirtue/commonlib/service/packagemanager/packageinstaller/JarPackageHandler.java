package net.maatvirtue.commonlib.service.packagemanager.packageinstaller;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.service.packagemanager.PackageRegistryService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class JarPackageHandler implements PackageHandler
{
	private PackageRegistryService packageRegistryService = PackageRegistryService.getInstance();

	@Override
	public void installPackage(Package pck) throws PackageManagerException
	{
		String packageName = pck.getMetadata().getName();
		Path applicationFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER.resolve(packageName);
		Path applicationJar = applicationFolder.resolve(packageName + ".jar");

		try
		{
			packageRegistryService.addPackage(pck.getMetadata());

			Files.createDirectories(applicationFolder);
			Files.write(applicationJar, pck.getInstallationData());

			Files.setPosixFilePermissions(applicationJar, getApplicationJarFilePermissions());

			executeJarWithCommand(applicationJar, PackageManagerConstants.APPLICATION_INSTALL_COMMAND);
		}
		catch(IOException | InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	@Override
	public void upgradePackage(Package pck) throws PackageManagerException
	{
		String packageName = pck.getMetadata().getName();
		Path applicationFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER.resolve(packageName);
		Path applicationJar = applicationFolder.resolve(packageName + ".jar");

		try
		{
			packageRegistryService.replacePackageMetadata(pck.getMetadata());

			Files.write(applicationJar, pck.getInstallationData());

			executeJarWithCommand(applicationJar, PackageManagerConstants.APPLICATION_UPGRADE_COMMAND);
		}
		catch(IOException | InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	@Override
	public void uninstallPackage(String packageName) throws PackageManagerException
	{
		Path applicationFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER.resolve(packageName);
		Path applicationJar = applicationFolder.resolve(packageName + ".jar");

		try
		{
			executeJarWithCommand(applicationJar, PackageManagerConstants.APPLICATION_UNINSTALL_COMMAND);

			FileUtils.deleteDirectory(applicationFolder.toFile());

			packageRegistryService.removePackage(packageName);
		}
		catch(IOException | InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private Set<PosixFilePermission> getApplicationJarFilePermissions()
	{
		Set<PosixFilePermission> permissions = new HashSet<>();

		permissions.add(PosixFilePermission.OWNER_READ);
		permissions.add(PosixFilePermission.OWNER_WRITE);
		permissions.add(PosixFilePermission.OWNER_EXECUTE);

		permissions.add(PosixFilePermission.GROUP_READ);
		permissions.add(PosixFilePermission.GROUP_EXECUTE);

		permissions.add(PosixFilePermission.OTHERS_READ);
		permissions.add(PosixFilePermission.OTHERS_EXECUTE);

		return permissions;
	}

	private void executeJarWithCommand(Path jar, String command) throws InterruptedException, IOException, PackageManagerException
	{
		Path applicationFolder = jar.getParent();
		String fullCommand = "java -jar " + jar.toAbsolutePath() + " " + command;

		Process process = Runtime.getRuntime().exec(fullCommand, null, applicationFolder.toFile());

		validateJarWithCommandExecution(process, command, fullCommand);
	}

	private void validateJarWithCommandExecution(Process process, String command, String fullCommand) throws InterruptedException, PackageManagerException, IOException
	{
		if(process.waitFor() != 0)
		{
			String processStdout = IOUtils.toString(process.getInputStream());
			String processStderr = IOUtils.toString(process.getErrorStream());

			String errorMessage = "";
			errorMessage += "Error calling JAR with " + command + "command: " + fullCommand + "\r\n";
			errorMessage += "STDERR: \r\n" + processStderr + "\r\n";
			errorMessage += "STDOUT: \r\n" + processStdout + "\r\n";

			throw new PackageManagerException(errorMessage);
		}
	}
}
