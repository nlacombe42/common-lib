package net.maatvirtue.commonlib.packagemanager.pkg;

public class Version
{
	private int majorVersion;
	private int minorVersion;
	private int patchVersion;
	private int buildVersion;
	private int packageVersion;
	private String displayName;

	public Version()
	{
		//Do nothing
	}

	public Version(int majorVersion, int minorVersion)
	{
		this(majorVersion, minorVersion, 0);
	}

	public Version(int majorVersion, int minorVersion, int patchVersion)
	{
		this(majorVersion, minorVersion, patchVersion, 0);
	}

	public Version(int majorVersion, int minorVersion, int patchVersion, int buildVersion)
	{
		this(majorVersion, minorVersion, patchVersion, buildVersion, 0);
	}

	public Version(int majorVersion, int minorVersion, int patchVersion, int buildVersion, int packageVersion)
	{
		this(majorVersion, minorVersion, patchVersion, buildVersion, packageVersion, null);
	}

	public Version(int majorVersion, int minorVersion, int patchVersion, int buildVersion, int packageVersion, String displayName)
	{
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.patchVersion = patchVersion;
		this.buildVersion = buildVersion;
		this.packageVersion = packageVersion;
		this.displayName = displayName;
	}

	public int getMajorVersion()
	{
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion)
	{
		this.majorVersion = majorVersion;
	}

	public int getMinorVersion()
	{
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion)
	{
		this.minorVersion = minorVersion;
	}

	public int getPatchVersion()
	{
		return patchVersion;
	}

	public void setPatchVersion(int patchVersion)
	{
		this.patchVersion = patchVersion;
	}

	public int getBuildVersion()
	{
		return buildVersion;
	}

	public void setBuildVersion(int buildVersion)
	{
		this.buildVersion = buildVersion;
	}

	public int getPackageVersion()
	{
		return packageVersion;
	}

	public void setPackageVersion(int packageVersion)
	{
		this.packageVersion = packageVersion;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
}
