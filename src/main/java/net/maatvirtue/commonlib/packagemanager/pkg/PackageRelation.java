package net.maatvirtue.commonlib.packagemanager.pkg;

public class PackageRelation
{
	private PackageRelationType relationType;
	private String packageName;
	private VersionRelationType versionRelationType;
	private Version packageVersion;

	public PackageRelation()
	{
		//Do nothing
	}

	public PackageRelation(PackageRelationType relationType, String packageName, VersionRelationType versionRelationType, Version packageVersion)
	{
		this.relationType = relationType;
		this.packageName = packageName;
		this.versionRelationType = versionRelationType;
		this.packageVersion = packageVersion;
	}

	public PackageRelationType getRelationType()
	{
		return relationType;
	}

	public void setRelationType(PackageRelationType relationType)
	{
		this.relationType = relationType;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public VersionRelationType getVersionRelationType()
	{
		return versionRelationType;
	}

	public void setVersionRelationType(VersionRelationType versionRelationType)
	{
		this.versionRelationType = versionRelationType;
	}

	public Version getPackageVersion()
	{
		return packageVersion;
	}

	public void setPackageVersion(Version packageVersion)
	{
		this.packageVersion = packageVersion;
	}
}
