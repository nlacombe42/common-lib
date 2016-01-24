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
import java.util.Collections;
import java.util.HashSet;

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
		Files.setPosixFilePermissions(applicationJar, new HashSet<>(Collections.singletonList(PosixFilePermission.OWNER_EXECUTE)));

		executeCommand(applicationJar);
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
