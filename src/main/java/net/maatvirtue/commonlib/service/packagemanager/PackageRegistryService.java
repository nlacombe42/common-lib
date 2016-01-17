package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.PackageRegistry;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.exception.PackageManagerRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class PackageRegistryService
{
	private static Logger logger = LoggerFactory.getLogger(PackageRegistryService.class);
	private static PackageRegistryService instance;

	private PackageRegistrySerializer packageRegistrySerializer = PackageRegistrySerializer.getInstance();

	private PackageRegistryService()
	{
		try
		{
			createRegistryIfNotPresent();
		}
		catch(Exception exception)
		{
			throw new RuntimeException("Error creating registry", exception);
		}
	}

	public static PackageRegistryService getInstance()
	{
		if(instance == null)
			instance = new PackageRegistryService();

		return instance;
	}

	public boolean isPackageInstalled(String packageName) throws PackageManagerException
	{
		try
		{
			PackageRegistry registry = loadRegistry();

			return registry.isPackageInRegistry(packageName);
		}
		catch(IOException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	public void addPackage(PackageMetadata packageMetadata) throws PackageManagerException
	{
		try
		{
			PackageRegistry registry = loadRegistry();

			registry.addPackageToRegistry(packageMetadata);

			saveRegistry(registry);
		}
		catch(IOException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	public void removePackage(String packageName) throws PackageManagerException
	{
		try
		{
			PackageRegistry registry = loadRegistry();

			registry.removePackage(packageName);

			saveRegistry(registry);
		}
		catch(IOException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private void createRegistryIfNotPresent() throws IOException, FfpdpException
	{
		Path packageManagerFolder = PackageManagerConstants.PACKAGE_MANAGER_FOLDER;
		Path lockFile = PackageManagerConstants.LOCK_FILE;

		if(!Files.exists(packageManagerFolder))
			Files.createDirectories(packageManagerFolder);

		try
		{
			Files.createFile(lockFile);
		}
		catch(FileAlreadyExistsException exception)
		{
			//Ignore
		}

		try(FileOutputStream fos = new FileOutputStream(lockFile.toFile()))
		{
			FileLock lock = fos.getChannel().tryLock();

			if(lock == null)
				throw new PackageManagerRuntimeException("Could not acquire lock");

			try
			{
				Path registryFile = PackageManagerConstants.REGISTRY_FILE;

				if(!Files.exists(registryFile))
					createRegistry(registryFile);
			}
			finally
			{
				lock.release();
			}
		}
	}

	private void createRegistry(Path registryFile) throws IOException, FfpdpException
	{
		Files.createFile(registryFile);

		saveRegistry(new PackageRegistry(new HashMap<String, PackageMetadata>()));
	}

	private PackageRegistry loadRegistry() throws IOException, FfpdpException, PackageManagerException
	{
		Path registryFile = PackageManagerConstants.REGISTRY_FILE;

		try(FileInputStream fis = new FileInputStream(registryFile.toFile()))
		{
			return packageRegistrySerializer.readRegistry(fis);
		}
	}

	private void saveRegistry(PackageRegistry registry) throws IOException, FfpdpException
	{
		Path registryFile = PackageManagerConstants.REGISTRY_FILE;

		try(FileOutputStream fos = new FileOutputStream(registryFile.toFile()))
		{
			packageRegistrySerializer.writeRegistry(fos, registry);
		}
	}
}
