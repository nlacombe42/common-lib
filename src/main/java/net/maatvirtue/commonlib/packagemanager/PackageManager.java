package net.maatvirtue.commonlib.packagemanager;

import net.maatvirtue.commonlib.crypto.CryptoUtil;
import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.ffpdp.FfpdpTag;
import net.maatvirtue.commonlib.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.ffpdp.FfpdpUtil;
import net.maatvirtue.commonlib.io.FrameInputStream;
import net.maatvirtue.commonlib.io.FrameOutputStream;
import net.maatvirtue.commonlib.packagemanager.pkg.*;
import net.maatvirtue.commonlib.packagemanager.pkg.Package;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class PackageManager
{
	private static final String PACKAGE_RELATION_SEPERATOR = ",";
	private static final String ENVIRONMENT_COMPATIBILITY_SEPERATOR = ",";
	private static final Pattern PACKAGE_NAME_REGX = Pattern.compile("^[a-z]+[a-z0-9\\-]*$");
	private static FfpdpTagV2 CURRENT_PACKAGE_FFPDP_TAG = new FfpdpTagV2(2, 1, 1, 0);

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

			FfpdpUtil.getInstance().writeFfpdpTag(fos, CURRENT_PACKAGE_FFPDP_TAG);

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

	public static Package readPackage(InputStream is) throws IOException, PackageManagerException
	{
		try(FrameInputStream fis = new FrameInputStream(is))
		{
			CryptoUtil cryptoUtil = CryptoUtil.getInstance();

			readAndVerifyFFpdpTag(fis);

			byte[] metadataBytes = fis.readFrame();
			PackageMetadata metadata = parsePackageMetadata(metadataBytes);
			PublicKey signerPublicKey = cryptoUtil.deserializePublicKey(fis.readFrame());
			byte[] signature = fis.readFrame();
			byte[] installationData = fis.readFrame();

			Package pck = new Package(metadata, installationData);

			if(validSignature(metadataBytes, installationData, signature, signerPublicKey))
				pck.setSignerPublickey(signerPublicKey);
			else
				pck.setSignerPublickey(null);

			return pck;
		}
		catch(CryptoException | FfpdpException exception)
		{
			throw new PackageManagerException(exception);
		}
	}

	private static void readAndVerifyFFpdpTag(InputStream is) throws IOException, FfpdpException, PackageManagerException
	{
		String errorMessage = "Not a V" + CURRENT_PACKAGE_FFPDP_TAG.getMajorVersion() + " package";

		FfpdpTag ffpdpTag = FfpdpUtil.getInstance().readFfpdpTag(is);

		if(ffpdpTag.getFfpdpVersion() != CURRENT_PACKAGE_FFPDP_TAG.getFfpdpVersion())
			throw new PackageManagerException(errorMessage);

		FfpdpTagV2 ffpdpTagV2 = (FfpdpTagV2) ffpdpTag;

		if(ffpdpTagV2.getUid() != CURRENT_PACKAGE_FFPDP_TAG.getUid() || ffpdpTagV2.getType() != CURRENT_PACKAGE_FFPDP_TAG.getType()
				|| ffpdpTagV2.getMajorVersion() != CURRENT_PACKAGE_FFPDP_TAG.getMajorVersion())
		{

			throw new PackageManagerException(errorMessage);
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

	public static boolean validSignature(byte[] metadata, byte[] installationData, byte[] signature, PublicKey signerPublicKey) throws CryptoException
	{
		if(signature.length==0)
			return false;

		byte[] metadataAndInstallationData = ArrayUtils.addAll(metadata, installationData);

		return CryptoUtil.getInstance().verifySha1Rsa(signerPublicKey, metadataAndInstallationData, signature);
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

	private static PackageMetadata parsePackageMetadata(byte[] metadataBytes) throws IOException
	{
		try(FrameInputStream fis = new FrameInputStream(new ByteArrayInputStream(metadataBytes)))
		{
			String packageName = readUtf8String(fis);
			Version packageVersion = new Version(readUtf8String(fis), readUtf8String(fis));

			PackageMetadata metadata = new PackageMetadata(packageName, packageVersion);

			metadata.setSoftwareAuthor(parseContact(readUtf8String(fis)));
			metadata.setPackageAuthor(parseContact(readUtf8String(fis)));
			metadata.setPackageRelations(splitPackgeRelations(readUtf8String(fis)));
			metadata.setEnvironmentCompatibilities(splitEnvironmentCompatibilities(readUtf8String(fis)));
			metadata.setInstallationDataType(InstallationDataType.getByCode(readUtf8String(fis)));

			return metadata;
		}
	}

	private static void writeUtf8String(FrameOutputStream fos, String text) throws IOException
	{
		if(text == null)
			text = "";

		fos.writeFrame(text.getBytes("UTF-8"));
	}

	private static String readUtf8String(FrameInputStream fis) throws IOException
	{
		String text = new String(fis.readFrame());

		if("".equals(text))
			return null;

		return text;
	}

	private static String getContactText(Contact contact)
	{
		if(contact == null)
			return "";
		else
			return contact.getContactText();
	}

	private static Contact parseContact(String contactText)
	{
		if("".equals(contactText))
			return null;

		return new Contact(contactText);
	}

	private static boolean validPackageName(String packageName)
	{
		return PACKAGE_NAME_REGX.matcher(packageName).matches();
	}

	private static String joinEnvironmentCompatibilities(Set<EnvironmentCompatibility> environmentCompatibilities)
	{
		String environmentCompatibilitiesText = "";

		for(EnvironmentCompatibility environmentCompatibility : environmentCompatibilities)
		{
			if(!environmentCompatibilitiesText.equals(""))
				environmentCompatibilitiesText += ENVIRONMENT_COMPATIBILITY_SEPERATOR;

			environmentCompatibilitiesText += environmentCompatibility.getEnvironmentCompatibilityText();
		}

		return environmentCompatibilitiesText;
	}

	private static Set<EnvironmentCompatibility> splitEnvironmentCompatibilities(String environmentCompatibilitiesText)
	{
		String[] environmentCompatibilitiesTexts = environmentCompatibilitiesText.split(PACKAGE_RELATION_SEPERATOR);
		Set<EnvironmentCompatibility> environmentCompatibilities = new HashSet<>(environmentCompatibilitiesTexts.length);

		for(String environmentCompatibilityText : environmentCompatibilitiesTexts)
			environmentCompatibilities.add(new EnvironmentCompatibility(environmentCompatibilityText));

		return environmentCompatibilities;
	}

	private static String joinPackgeRelations(Set<PackageRelation> packageRelations)
	{
		String packageRelationsText = "";

		for(PackageRelation packageRelation : packageRelations)
		{
			if(!packageRelationsText.equals(""))
				packageRelationsText += PACKAGE_RELATION_SEPERATOR;

			packageRelationsText += packageRelation.getPackageRelationText();
		}

		return packageRelationsText;
	}

	private static Set<PackageRelation> splitPackgeRelations(String packageRelationsText)
	{
		String[] packageRelationsTexts = packageRelationsText.split(PACKAGE_RELATION_SEPERATOR);
		Set<PackageRelation> packageRelations = new HashSet<>(packageRelationsTexts.length);

		for(String packageRelationText : packageRelationsTexts)
			packageRelations.add(new PackageRelation(packageRelationText));

		return packageRelations;
	}
}
