package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.exception.NotInstalledPackageManagerException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.exception.PackageManagerRuntimeException;
import net.maatvirtue.commonlib.service.crypto.CryptoService;
import net.maatvirtue.commonlib.service.packagemanager.packageinstaller.PackageHandler;
import net.maatvirtue.commonlib.service.packagemanager.packageinstaller.PackageHandlerFactory;
import org.apache.commons.io.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PublicKey;
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

		PackageHandler packageHandler =
				PackageHandlerFactory.getPackageHandler(pck.getMetadata().getInstallationType());

		packageHandler.installPackage(pck);
	}

	private void uninstallWithoutLock(String packageName) throws IOException, PackageManagerException, InterruptedException
	{
		if(!packageRegistryService.isPackageInstalled(packageName))
			throw new NotInstalledPackageManagerException(packageName);

		PackageMetadata packageMetadata = packageRegistryService.getPackageMetadata(packageName);

		PackageHandler packageHandler =
				PackageHandlerFactory.getPackageHandler(packageMetadata.getInstallationType());

		packageHandler.uninstallPackage(packageName);
	}

	private void validatePackageSignature(PublicKey packageSignature) throws IOException, PackageManagerException
	{
		PublicKey rootSignignPublicKey = getRootSigningPublicKey();

		if(!rootSignignPublicKey.equals(packageSignature))
			throw new PackageManagerException("Package not signed by root package manager public key.");
	}

	private PublicKey getRootSigningPublicKey() throws IOException
	{
		InputStream is = getClass().getResourceAsStream("/" + PackageManagerConstants.PACKAGE_MANAGER_ROOT_SIGNING_PUBLIC_KEY_FILENAME);

		return cryptoService.readPublicKeyFromPem(new InputStreamReader(is));
	}
}
