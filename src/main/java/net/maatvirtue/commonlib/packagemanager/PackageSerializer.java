package net.maatvirtue.commonlib.packagemanager;

import net.maatvirtue.commonlib.crypto.CryptoUtil;
import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.ffpdp.FfpdpUtil;
import net.maatvirtue.commonlib.io.FrameOutputStream;
import net.maatvirtue.commonlib.packagemanager.pkg.*;
import net.maatvirtue.commonlib.packagemanager.pkg.Package;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.Set;

public class PackageSerializer
{
	public static void writePackage(OutputStream os, Package pkg, KeyPair signingKeypair) throws IOException, PackageManagerException
	{
		if(!validPackageName(pkg.getMetadata().getName()))
			throw new IllegalArgumentException("invalid package name: " + pkg.getMetadata().getName());

		try(FrameOutputStream fos = new FrameOutputStream(os))
		{
			CryptoUtil cryptoUtil = CryptoUtil.getInstance();

			byte[] metadata = getMetadataBytes(pkg.getMetadata());
			byte[] installationData = pkg.getInstallationData();
			byte[] signature = signPackage(metadata, installationData, pkg, signingKeypair);

			FfpdpUtil.getInstance().writeFfpdpTag(fos, PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG);

			fos.writeFrame(metadata);
			fos.writeFrame(cryptoUtil.serializePublicKey(signingKeypair.getPublic()));
			fos.writeFrame(signature);
			fos.writeFrame(installationData);
		}
		catch(CryptoException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private static byte[] signPackage(byte[] metadata, byte[] installationData, Package pkg, KeyPair signingKeypair) throws CryptoException
	{
		byte[] metadataAndInstallationData = ArrayUtils.addAll(metadata, installationData);

		byte[] signature;

		if(signingKeypair != null)
		{
			CryptoUtil cryptoUtil = CryptoUtil.getInstance();

			signature = cryptoUtil.signSha1Rsa(signingKeypair.getPrivate(), metadataAndInstallationData);

			pkg.setSignerPublickey(signingKeypair.getPublic());
		}
		else
			signature = new byte[0];

		return signature;
	}

	private static byte[] getMetadataBytes(PackageMetadata packageMetadata) throws IOException
	{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FrameOutputStream fos = new FrameOutputStream(baos))
		{
			writeUtf8String(fos, packageMetadata.getName());
			writeUtf8String(fos, packageMetadata.getVersion().getVersionText());
			writeUtf8String(fos, packageMetadata.getVersion().getDisplayName());
			writeUtf8String(fos, getContactText(packageMetadata.getSoftwareAuthor()));
			writeUtf8String(fos, getContactText(packageMetadata.getPackageAuthor()));
			writeUtf8String(fos, joinPackgeRelations(packageMetadata.getPackageRelations()));
			writeUtf8String(fos, joinEnvironmentCompatibilities(packageMetadata.getEnvironmentCompatibilities()));
			writeUtf8String(fos, packageMetadata.getInstallationDataType().getCode());

			return baos.toByteArray();
		}
	}

	private static void writeUtf8String(FrameOutputStream fos, String text) throws IOException
	{
		if(text == null)
			text = "";

		fos.writeFrame(text.getBytes("UTF-8"));
	}

	private static String getContactText(Contact contact)
	{
		if(contact == null)
			return "";
		else
			return contact.getContactText();
	}

	private static boolean validPackageName(String packageName)
	{
		return PackageManagerConstants.PACKAGE_NAME_REGX.matcher(packageName).matches();
	}

	private static String joinEnvironmentCompatibilities(Set<EnvironmentCompatibility> environmentCompatibilities)
	{
		String environmentCompatibilitiesText = "";

		for(EnvironmentCompatibility environmentCompatibility : environmentCompatibilities)
		{
			if(!environmentCompatibilitiesText.equals(""))
				environmentCompatibilitiesText += PackageManagerConstants.ENVIRONMENT_COMPATIBILITY_SEPERATOR;

			environmentCompatibilitiesText += environmentCompatibility.getEnvironmentCompatibilityText();
		}

		return environmentCompatibilitiesText;
	}

	private static String joinPackgeRelations(Set<PackageRelation> packageRelations)
	{
		String packageRelationsText = "";

		for(PackageRelation packageRelation : packageRelations)
		{
			if(!packageRelationsText.equals(""))
				packageRelationsText += PackageManagerConstants.PACKAGE_RELATION_SEPERATOR;

			packageRelationsText += packageRelation.getPackageRelationText();
		}

		return packageRelationsText;
	}
}
