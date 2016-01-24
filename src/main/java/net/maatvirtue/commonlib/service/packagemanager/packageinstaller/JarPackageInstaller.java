package net.maatvirtue.commonlib.service.packagemanager.packageinstaller;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.service.packagemanager.PackageRegistryService;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class JarPackageInstaller implements PackageInstaller
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

			createApplicationFolderAndCopyFiles(applicationJar, pck.getInstallationData());

			executeInstallTrigger(applicationJar);
		}
		catch(IOException|InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private void executeInstallTrigger(Path applicationJar) throws IOException, InterruptedException, PackageManagerException
	{
		Files.setPosixFilePermissions(applicationJar, getApplicationJarFilePermissions());

		executeCommand(applicationJar);
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

	private void executeCommand(Path applicationJar) throws InterruptedException, IOException, PackageManagerException
	{
		Path applicationFolder = applicationJar.getParent();
		String command = "java -jar " + applicationJar.toAbsolutePath() + " install";

		Process process = Runtime.getRuntime().exec(command, null, applicationFolder.toFile());

		validateCommandExecution(process, command);
	}

	private void validateCommandExecution(Process process, String command) throws InterruptedException, PackageManagerException, IOException
	{
		if(process.waitFor() != 0)
		{
			String processStdout = IOUtils.toString(process.getInputStream());
			String processStderr = IOUtils.toString(process.getErrorStream());

			String errorMessage = "";
			errorMessage += "Error calling JAR with install command: " + command + "\r\n";
			errorMessage += "STDERR: \r\n"+processStderr+"\r\n";
			errorMessage += "STDOUT: \r\n"+processStdout+"\r\n";

			throw new PackageManagerException(errorMessage);
		}
	}

	private void createApplicationFolderAndCopyFiles(Path applicationJar, byte[] installationData) throws IOException
	{
		Path applicationFolder = applicationJar.getParent();

		Files.createDirectories(applicationFolder);
		Files.write(applicationJar, installationData);
	}
}
