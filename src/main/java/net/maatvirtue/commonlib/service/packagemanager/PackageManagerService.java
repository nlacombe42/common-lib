package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.domain.packagemanager.Package;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;

@Service
public class PackageManagerService
{
	@Inject
	private PackageSerializer packageSerializer;

	@Inject
	private PackageDeserializer packageDeserializer;

	public void writePackage(OutputStream os, Package pkg, KeyPair signingKeypair) throws IOException, PackageManagerException
	{
		packageSerializer.writePackage(os, pkg, signingKeypair);
	}

	public Package readPackage(InputStream is) throws IOException, PackageManagerException
	{
		return packageDeserializer.readPackage(is);
	}
}
