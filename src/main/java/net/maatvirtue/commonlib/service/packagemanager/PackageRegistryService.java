package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.PackageRegistry;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.exception.PackageManagerRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@Service
public class PackageRegistryService
{
	private Logger logger = LoggerFactory.getLogger(PackageRegistryService.class);

	@Inject
	private PackageManagerService packageManagerService;

	@Inject
	private PackageRegistrySerializer packageRegistrySerializer;

	public PackageRegistryService()
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

	public boolean isPackageInstalled(String packageName) throws PackageManagerException
	{
		try
		{
			PackageRegistry registry = loadRegistry();

			return registry.isPackageInRegistry(packageName);
		}
		catch(IOException|FfpdpException exception)
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
		catch(IOException|FfpdpException exception)
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
		catch(IOException|FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private void createRegistryIfNotPresent() throws IOException, FfpdpException
	{
		Path packageManagerFolder = packageManagerService.getPackageManagerFolder();
		Path lockFile = packageManagerService.getLockFile();

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

			if(lock==null)
				throw new PackageManagerRuntimeException("Could not acquire lock");

			try
			{
				createRegistry();
			}
			finally
			{
				lock.release();
			}
		}
	}

	private void createRegistry() throws IOException, FfpdpException
	{
		Path registryFile = getRegistryFile();

		Files.createFile(registryFile);

		saveRegistry(new PackageRegistry(new HashMap<>()));
	}

	private PackageRegistry loadRegistry() throws IOException, FfpdpException, PackageManagerException
	{
		Path registryFile = getRegistryFile();

		try(FileInputStream fis = new FileInputStream(registryFile.toFile()))
		{
			return packageRegistrySerializer.readRegistry(fis);
		}
	}

	private void saveRegistry(PackageRegistry registry) throws IOException, FfpdpException
	{
		Path registryFile = getRegistryFile();

		try(FileOutputStream fos = new FileOutputStream(registryFile.toFile()))
		{
			packageRegistrySerializer.writeRegistry(fos, registry);
		}
	}

	private Path getRegistryFile()
	{
		return packageManagerService.getPackageManagerFolder().resolve(PackageManagerConstants.REGISTRY_FILE_NAME);
	}
}
