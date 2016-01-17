package net.maatvirtue.commonlib.service.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.packagemanager.PackageRegistry;
import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;
import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.PackageManagerException;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpService;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpTag;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.service.ffpdp.FfpdpVersion;
import net.maatvirtue.commonlib.util.io.FrameInputStream;
import net.maatvirtue.commonlib.util.io.FrameOutputStream;
import net.maatvirtue.commonlib.util.io.IoUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class PackageRegistrySerializer
{
	@Inject
	private FfpdpService ffpdpService;

	@Inject
	private PackageSerializer packageSerializer;

	@Inject
	private PackageDeserializer packageDeserializer;

	public void writeRegistry(OutputStream os, PackageRegistry registry) throws IOException, FfpdpException
	{
		try(FrameOutputStream fos = new FrameOutputStream(os))
		{
			ffpdpService.writeFfpdpTag(fos, PackageManagerConstants.CURRENT_REGISTRY_FFPDP_TAG);
			fos.write("\r\n".getBytes());

			IoUtil.writeInteger(fos, registry.getNumberOfPackages());

			for(String packageName : registry.getPackageNames())
				fos.writeFrame(packageSerializer.getMetadataBytes(registry.getPackageMetadata(packageName)));
		}
	}

	public PackageRegistry readRegistry(InputStream is) throws IOException, FfpdpException, PackageManagerException
	{
		try(FrameInputStream fis = new FrameInputStream(is))
		{
			readAndCheckFfpdpTag(fis);
			fis.read();

			int numberOfPackages = IoUtil.readInteger(fis);
			Map<String, PackageMetadata> registryMap = new HashMap<>(numberOfPackages);

			for(int i=0; i<=numberOfPackages; i++)
			{
				PackageMetadata packageMetadata = packageDeserializer.parsePackageMetadata(fis.readFrame());

				registryMap.put(packageMetadata.getName(), packageMetadata);
			}

			return new PackageRegistry(registryMap);
		}
	}

	private void readAndCheckFfpdpTag(InputStream is) throws IOException, FfpdpException, PackageManagerException
	{
		FfpdpTagV2 currentFfpdpTag = PackageManagerConstants.CURRENT_REGISTRY_FFPDP_TAG;
		int registryCurrentMajorVersion = currentFfpdpTag.getMajorVersion();

		FfpdpTag ffpdpTag = ffpdpService.readFfpdpTag(is);

		if(ffpdpTag.getFfpdpVersion()!= FfpdpVersion.V2)
			throw new PackageManagerException("Not a package registry V"+registryCurrentMajorVersion);

		FfpdpTagV2 ffpdpTagV2 = (FfpdpTagV2) ffpdpTag;

		if(ffpdpTagV2.getUid() != currentFfpdpTag.getUid() || ffpdpTagV2.getType() != currentFfpdpTag.getType() ||
				ffpdpTagV2.getMajorVersion() != currentFfpdpTag.getMajorVersion())
			throw new PackageManagerException("Not a package registry V"+registryCurrentMajorVersion);
	}
}
