package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Contact;
import net.maatvirtue.commonlib.domain.packagemanager.pck.EnvironmentCompatibility;
import net.maatvirtue.commonlib.domain.packagemanager.pck.InstallationType;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageRelation;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Version;
import net.maatvirtue.commonlib.service.crypto.CryptoService;
import net.maatvirtue.commonlib.exception.CryptoException;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpTag;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpService;
import net.maatvirtue.commonlib.domain.packagemanager.pck.Package;
import net.maatvirtue.commonlib.util.io.FrameInputStream;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

public class PackageDeserializer
{
	private CryptoService cryptoService = CryptoService.getInstance();
	private FfpdpService ffpdpService = FfpdpService.getInstance();

	private static PackageDeserializer instance;

	public PackageDeserializer()
	{
		//Do nothing
	}

	public static PackageDeserializer getInstance()
	{
		if(instance == null)
			instance = new PackageDeserializer();

		return instance;
	}

	public Package readPackage(InputStream is) throws IOException, PackageManagerException
	{
		try(FrameInputStream fis = new FrameInputStream(is))
		{
			readAndVerifyFFpdpTag(fis);

			byte[] metadataBytes = fis.readFrame();
			PackageMetadata metadata = parsePackageMetadata(metadataBytes);
			PublicKey signerPublicKey = cryptoService.deserializePublicKey(fis.readFrame());
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

	private void readAndVerifyFFpdpTag(InputStream is) throws IOException, FfpdpException, PackageManagerException
	{
		String errorMessage = "Not a V" + PackageManagerConstants.CURRENT_PACKAGE_FFPDP_TAG.getMajorVersion() + " package";

		FfpdpTag ffpdpTag = ffpdpService.readFfpdpTag(is);

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

	public boolean validSignature(byte[] metadata, byte[] installationData, byte[] signature, PublicKey signerPublicKey) throws CryptoException
	{
		if(signature.length == 0)
			return false;

		byte[] metadataAndInstallationData = ArrayUtils.addAll(metadata, installationData);

		return cryptoService.verifySha1Rsa(signerPublicKey, metadataAndInstallationData, signature);
	}

	public PackageMetadata parsePackageMetadata(byte[] metadataBytes) throws IOException
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
			metadata.setInstallationType(InstallationType.getByCode(readUtf8String(fis)));

			return metadata;
		}
	}

	private String readUtf8String(FrameInputStream fis) throws IOException
	{
		String text = new String(fis.readFrame());

		if("".equals(text))
			return null;

		return text;
	}

	private Contact parseContact(String contactText)
	{
		if("".equals(contactText))
			return null;

		return new Contact(contactText);
	}

	private Set<EnvironmentCompatibility> splitEnvironmentCompatibilities(String environmentCompatibilitiesText)
	{
		if(environmentCompatibilitiesText==null)
			return null;

		String[] environmentCompatibilitiesTexts = environmentCompatibilitiesText.split(PackageManagerConstants.PACKAGE_RELATION_SEPERATOR);
		Set<EnvironmentCompatibility> environmentCompatibilities = new HashSet<>(environmentCompatibilitiesTexts.length);

		for(String environmentCompatibilityText : environmentCompatibilitiesTexts)
			environmentCompatibilities.add(new EnvironmentCompatibility(environmentCompatibilityText));

		return environmentCompatibilities;
	}

	private Set<PackageRelation> splitPackgeRelations(String packageRelationsText)
	{
		if(packageRelationsText==null)
			return null;

		String[] packageRelationsTexts = packageRelationsText.split(PackageManagerConstants.PACKAGE_RELATION_SEPERATOR);
		Set<PackageRelation> packageRelations = new HashSet<>(packageRelationsTexts.length);

		for(String packageRelationText : packageRelationsTexts)
			packageRelations.add(new PackageRelation(packageRelationText));

		return packageRelations;
	}
}
