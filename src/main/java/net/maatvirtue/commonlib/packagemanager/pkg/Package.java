package net.maatvirtue.commonlib.packagemanager.pkg;

import java.util.Set;

public class Package
{
	private int packageMetaMajorVersion;
	private int packageMetaMinorVersion;
	private String name;
	private Version version;
	private Contact packageAuthor;
	private Contact softwareAuthor;
	private Set<EnvironmentCompatibility> environmentCompatibilities;
	private boolean isVirtualPackage;
	private Set<PackageRelation> packageRelations;
	private InstallationDataType installationDataType;
	private byte[] installationData;
	private byte[] signerPublickey;
	private byte[] signature;
}
