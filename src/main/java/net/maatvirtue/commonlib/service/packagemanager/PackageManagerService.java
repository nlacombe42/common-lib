package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.pck.InstallationDataType;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.exception.NotImplementedPackageManagerException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.exception.PackageManagerRuntimeException;
import net.maatvirtue.commonlib.service.crypto.CryptoService;
import org.apache.commons.io.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageManagerService
{
	private static PackageManagerService instance;

	private PackageSerializer packageSerializer = PackageSerializer.getInstance();
	private PackageDeserializer packageDeserializer = PackageDeserializer.getInstance();
	private PackageRegistryService packageRegistryService = PackageRegistryService.getInstance();
	private CryptoService cryptoService = CryptoService.getInstance();

	private PackageManagerService()
	{
		//Do nothing
	}

	public static PackageManagerService getInstance()
	{
		if(instance == null)
			instance = new PackageManagerService();

		return instance;
	}

	public void writePackage(OutputStream os, Package pkg, KeyPair signingKeypair) throws IOException, PackageManagerException
	{
		packageSerializer.writePackage(os, pkg, signingKeypair);
	}

	public Package readPackage(InputStream is) throws IOException, PackageManagerException
	{
		return packageDeserializer.readPackage(is);
	}

	public void install(Package pck) throws PackageManagerException
	{
		try
		{
			validatePackageSignature(pck.getSignerPublickey());
		}
		catch(IOException exception)
		{
			throw new PackageManagerException(exception);
		}

		Path lockFile = PackageManagerConstants.LOCK_FILE;

		try(FileOutputStream fos = new FileOutputStream(lockFile.toFile()))
		{
			FileLock lock = fos.getChannel().tryLock();

			if(lock == null)
				throw new PackageManagerRuntimeException("Could not acquire lock");

			try
			{
				installWithoutLock(pck);
			}
			finally
			{
				lock.release();
			}
		}
		catch(IOException | InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	public void uninstall(String packageName) throws PackageManagerException
	{
		Path lockFile = PackageManagerConstants.LOCK_FILE;

		try(FileOutputStream fos = new FileOutputStream(lockFile.toFile()))
		{
			FileLock lock = fos.getChannel().tryLock();

			if(lock == null)
				throw new PackageManagerRuntimeException("Could not acquire lock");

			try
			{
				uninstallWithoutLock(packageName);
			}
			finally
			{
				lock.release();
			}
		}
		catch(IOException | InterruptedException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	public boolean isPackageManagerRootSigningKey(PublicKey publicKey) throws PackageManagerException
	{
		try
		{
			return getRootSigningPublicKey().equals(publicKey);
		}
		catch(IOException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	public Set<PackageMetadata> getInstalledPackages() throws PackageManagerException
	{
		Path lockFile = PackageManagerConstants.LOCK_FILE;

		try(FileOutputStream fos = new FileOutputStream(lockFile.toFile()))
		{
			FileLock lock = fos.getChannel().tryLock();

			if(lock == null)
				throw new PackageManagerRuntimeException("Could not acquire lock");

			try
			{
				return packageRegistryService.getInstalledPackages();
			}
			finally
			{
				lock.release();
			}
		}
		catch(IOException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private void installWithoutLock(Package pck) throws PackageManagerException, IOException, InterruptedException
	{
		String packageName = pck.getMetadata().getName();

		if(packageRegistryService.isPackageInstalled(packageName))
			uninstallWithoutLock(packageName);

		if(pck.getMetadata().getInstallationDataType() == InstallationDataType.JAR)
			installJarPackage(pck);
		else
			throw new NotImplementedPackageManagerException("Unknown installation type: " + pck.getMetadata().getInstallationDataType());
	}

	private void installJarPackage(Package pck) throws IOException, PackageManagerException, InterruptedException
	{
		String packageName = pck.getMetadata().getName();

		Path applicationFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER.resolve(packageName);
		Path applicationJar = applicationFolder.resolve(packageName + ".jar");

		packageRegistryService.addPackage(pck.getMetadata());

		Files.createDirectories(applicationFolder);

		try(FileOutputStream fos = new FileOutputStream(applicationJar.toFile()))
		{
			fos.write(pck.getInstallationData());
			fos.flush();
		}

		Files.setPosixFilePermissions(applicationJar, new HashSet<>(Collections.singletonList(PosixFilePermission.OWNER_EXECUTE)));

		Process process = Runtime.getRuntime().exec("java -jar " + applicationJar.toAbsolutePath() + " install",
				null, applicationFolder.toFile());

		if(process.waitFor() != 0)
			throw new PackageManagerException("Error calling JAR with install command");
	}

	private void uninstallWithoutLock(String packageName) throws IOException, PackageManagerException, InterruptedException
	{
		Path applicationFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER.resolve(packageName);
		Path applicationJar = applicationFolder.resolve(packageName + ".jar");

		Process process = Runtime.getRuntime().exec("java -jar " + applicationJar.toAbsolutePath() + " uninstall",
				null, applicationFolder.toFile());

		if(process.waitFor() != 0)
			throw new PackageManagerException("Error calling JAR with uninstall command");

		FileUtils.deleteDirectory(applicationFolder.toFile());

		packageRegistryService.removePackage(packageName);
	}

	private void validatePackageSignature(PublicKey packageSignature) throws IOException, PackageManagerException
	{
		PublicKey rootSignignPublicKey = getRootSigningPublicKey();

		if(!rootSignignPublicKey.equals(packageSignature))
			throw new PackageManagerException("Package not signed by root package manager public key.");
	}

	private PublicKey getRootSigningPublicKey() throws IOException
	{
		InputStream is = getClass().getResourceAsStream("/"+PackageManagerConstants.PACKAGE_MANAGER_ROOT_SIGNING_PUBLIC_KEY_FILENAME);

		return cryptoService.readPublicKeyFromPem(new InputStreamReader(is));
	}
}
