package net.maatvirtue.commonlib.packagemanager;

import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.packagemanager.pkg.Package;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;

public class PackageManager
{
	public static void writePackage(OutputStream os, Package pkg, KeyPair signingKeypair) throws IOException, PackageManagerException
	{
		PackageSerializer.writePackage(os, pkg, signingKeypair);
	}

	public static Package readPackage(InputStream is) throws IOException, PackageManagerException
	{
		return PackageDeserializer.readPackage(is);
	}
}
