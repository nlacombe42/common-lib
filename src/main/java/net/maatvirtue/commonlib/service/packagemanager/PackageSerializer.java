package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.Contact;
import net.maatvirtue.commonlib.domain.packagemanager.EnvironmentCompatibility;
import net.maatvirtue.commonlib.domain.packagemanager.PackageMetadata;
import net.maatvirtue.commonlib.domain.packagemanager.PackageRelation;
import net.maatvirtue.commonlib.service.crypto.CryptoService;
import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpService;
import net.maatvirtue.commonlib.domain.packagemanager.Package;
import net.maatvirtue.commonlib.util.io.FrameOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.util.Set;

@Service
public class PackageSerializer
{
	@Inject
	private CryptoService cryptoService;

	@Inject
	private FfpdpService ffpdpService;

	public void writePackage(OutputStream os, Package pkg, KeyPair signingKeypair) throws IOException, PackageManagerException
	{
		if(!validPackageName(pkg.getMetadata().getName()))
			throw new IllegalArgumentException("invalid package name: " + pkg.getMetadata().getName());

		try(FrameOutputStream fos = new FrameOutputStream(os))
		{
			byte[] metadata = getMetadataBytes(pkg.getMetadata());
			byte[] installationData = pkg.getInstallationData();
			byte[] signature = signPackage(metadata, installationData, pkg, signingKeypair);

			ffpdpService.writeFfpdpTag(fos, PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG);

			fos.writeFrame(metadata);
			fos.writeFrame(cryptoService.serializePublicKey(signingKeypair.getPublic()));
			fos.writeFrame(signature);
			fos.writeFrame(installationData);
		}
		catch(CryptoException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private byte[] signPackage(byte[] metadata, byte[] installationData, Package pkg, KeyPair signingKeypair) throws CryptoException
	{
		byte[] metadataAndInstallationData = ArrayUtils.addAll(metadata, installationData);

		byte[] signature;

		if(signingKeypair != null)
		{
			signature = cryptoService.signSha1Rsa(signingKeypair.getPrivate(), metadataAndInstallationData);

			pkg.setSignerPublickey(signingKeypair.getPublic());
		}
		else
			signature = new byte[0];

		return signature;
	}

	private byte[] getMetadataBytes(PackageMetadata packageMetadata) throws IOException
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

	private void writeUtf8String(FrameOutputStream fos, String text) throws IOException
	{
		if(text == null)
			text = "";

		fos.writeFrame(text.getBytes("UTF-8"));
	}

	private String getContactText(Contact contact)
	{
		if(contact == null)
			return "";
		else
			return contact.getContactText();
	}

	private boolean validPackageName(String packageName)
	{
		return PackageManagerConstants.PACKAGE_NAME_REGX.matcher(packageName).matches();
	}

	private String joinEnvironmentCompatibilities(Set<EnvironmentCompatibility> environmentCompatibilities)
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

	private String joinPackgeRelations(Set<PackageRelation> packageRelations)
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
