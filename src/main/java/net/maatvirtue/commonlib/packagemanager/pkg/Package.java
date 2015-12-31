package net.maatvirtue.commonlib.packagemanager.pkg;

public class Package
{
	private PackageMetadata metadata;
	private byte[] installationData;
	private byte[] signerPublickey;

	public Package()
	{
		//Do nothing
	}

	public Package(PackageMetadata metadata, byte[] installationData)
	{
		this(metadata, installationData, null);
	}

	public Package(PackageMetadata metadata, byte[] installationData, byte[] signerPublickey)
	{
		this.metadata =	metadata;
		this.installationData = installationData;
		this.signerPublickey = signerPublickey;
	}

	public boolean isSignatureValid()
	{
		return signerPublickey != null;
	}

	public PackageMetadata getMetadata()
	{
		return metadata;
	}

	public void setMetadata(PackageMetadata metadata)
	{
		this.metadata = metadata;
	}

	public byte[] getInstallationData()
	{
		return installationData;
	}

	public void setInstallationData(byte[] installationData)
	{
		this.installationData = installationData;
	}

	public byte[] getSignerPublickey()
	{
		return signerPublickey;
	}

	public void setSignerPublickey(byte[] signerPublickey)
	{
		this.signerPublickey = signerPublickey;
	}
}
