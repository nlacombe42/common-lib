package net.maatvirtue.commonlib.packagemanager;

import net.maatvirtue.commonlib.crypto.CryptoUtil;
import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.ffpdp.FfpdpTag;
import net.maatvirtue.commonlib.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.ffpdp.FfpdpUtil;
import net.maatvirtue.commonlib.io.FrameInputStream;
import net.maatvirtue.commonlib.packagemanager.pkg.*;
import net.maatvirtue.commonlib.packagemanager.pkg.Package;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

public class PackageDeserializer
{
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
		String errorMessage = "Not a V" + PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG.getMajorVersion() + " package";

		FfpdpTag ffpdpTag = FfpdpUtil.getInstance().readFfpdpTag(is);

		if(ffpdpTag.getFfpdpVersion() != PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG.getFfpdpVersion())
			throw new PackageManagerException(errorMessage);

		FfpdpTagV2 ffpdpTagV2 = (FfpdpTagV2) ffpdpTag;
		FfpdpTagV2 currentPackageFfpdptag = PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG;

		if(ffpdpTagV2.getUid() != currentPackageFfpdptag.getUid() || ffpdpTagV2.getType() != currentPackageFfpdptag.getType()
				|| ffpdpTagV2.getMajorVersion() != currentPackageFfpdptag.getMajorVersion())
		{

			throw new PackageManagerException(errorMessage);
		}
	}

	public static boolean validSignature(byte[] metadata, byte[] installationData, byte[] signature, PublicKey signerPublicKey) throws CryptoException
	{
		if(signature.length == 0)
			return false;

		byte[] metadataAndInstallationData = ArrayUtils.addAll(metadata, installationData);

		return CryptoUtil.getInstance().verifySha1Rsa(signerPublicKey, metadataAndInstallationData, signature);
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

	private static String readUtf8String(FrameInputStream fis) throws IOException
	{
		String text = new String(fis.readFrame());

		if("".equals(text))
			return null;

		return text;
	}

	private static Contact parseContact(String contactText)
	{
		if("".equals(contactText))
			return null;

		return new Contact(contactText);
	}

	private static Set<EnvironmentCompatibility> splitEnvironmentCompatibilities(String environmentCompatibilitiesText)
	{
		String[] environmentCompatibilitiesTexts = environmentCompatibilitiesText.split(PackageManagerConstants.PACKAGE_RELATION_SEPERATOR);
		Set<EnvironmentCompatibility> environmentCompatibilities = new HashSet<>(environmentCompatibilitiesTexts.length);

		for(String environmentCompatibilityText : environmentCompatibilitiesTexts)
			environmentCompatibilities.add(new EnvironmentCompatibility(environmentCompatibilityText));

		return environmentCompatibilities;
	}

	private static Set<PackageRelation> splitPackgeRelations(String packageRelationsText)
	{
		String[] packageRelationsTexts = packageRelationsText.split(PackageManagerConstants.PACKAGE_RELATION_SEPERATOR);
		Set<PackageRelation> packageRelations = new HashSet<>(packageRelationsTexts.length);

		for(String packageRelationText : packageRelationsTexts)
			packageRelations.add(new PackageRelation(packageRelationText));

		return packageRelations;
	}
}
