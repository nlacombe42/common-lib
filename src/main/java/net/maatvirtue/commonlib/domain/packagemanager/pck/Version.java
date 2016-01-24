package net.maatvirtue.commonlib.domain.packagemanager.pck;

import org.apache.commons.lang3.StringUtils;

public class Version implements Comparable<Version>
{
	private static final String MAIN_VERSION_SEPERATOR = ".";
	private static final String PACKAGE_VERSION_SEPERATOR = "-";

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

	public Version(String versionText)
	{
		this(versionText, null);
	}

	public Version(String versionText, String displayName)
	{
		this.displayName = displayName;

		parseAndSetVersionText(versionText);
	}

	public boolean isEarlierOrEqualTo(Version version)
	{
		return compareTo(version) >= 0;
	}

	public boolean isEqualTo(Version version)
	{
		return compareTo(version) == 0;
	}

	public boolean isLaterOrEqualTo(Version version)
	{
		return compareTo(version) <= 0;
	}

	public boolean isStrictlyEarlierThan(Version version)
	{
		return compareTo(version) > 0;
	}

	public boolean isStrictlyLaterThan(Version version)
	{
		return compareTo(version) > 0;
	}

	private void parseAndSetVersionText(String versionText)
	{
		if(versionText==null)
			throw new IllegalArgumentException("invalid versionText");

		String mainVersion;
		String packageVersion = null;

		if(versionText.contains(PACKAGE_VERSION_SEPERATOR))
		{
			String[] versionParts = versionText.split("\\"+PACKAGE_VERSION_SEPERATOR);

			if(versionParts.length<2)
				throw new IllegalArgumentException("invalid versionText");

			mainVersion = versionParts[0];
			packageVersion = versionParts[1];
		}
		else
			mainVersion = versionText;

		parseAndSetMainVersion(mainVersion);

		if(packageVersion!=null)
			parseAndSetPackageVersion(packageVersion);
		else
			this.packageVersion = 0;
	}

	private void parseAndSetMainVersion(String mainVersionText)
	{
		String[] versionParts = mainVersionText.split("\\"+MAIN_VERSION_SEPERATOR);

		if(versionParts.length<3||versionParts.length>4)
			throw new IllegalArgumentException("invalid versionText");

		try
		{
			this.majorVersion = Integer.parseInt(versionParts[0]);
			this.minorVersion = Integer.parseInt(versionParts[1]);
			this.patchVersion = Integer.parseInt(versionParts[2]);

			if(versionParts.length>=4)
				this.buildVersion = Integer.parseInt(versionParts[3]);
			else
				this.buildVersion = 0;
		}
		catch(IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("invalid versionText", exception);
		}
	}

	private void parseAndSetPackageVersion(String packageVersionText)
	{
		try
		{
			this.packageVersion = Integer.parseInt(packageVersionText);
		}
		catch(IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("invalid versionText", exception);
		}
	}

	public String getVersionText()
	{
		String versionText = "";

		versionText += majorVersion;
		versionText += MAIN_VERSION_SEPERATOR;
		versionText += minorVersion;
		versionText += MAIN_VERSION_SEPERATOR;
		versionText += patchVersion;

		if(buildVersion!=0)
		{
			versionText += MAIN_VERSION_SEPERATOR;
			versionText += buildVersion;
		}

		if(packageVersion!=0)
		{
			versionText += PACKAGE_VERSION_SEPERATOR;
			versionText += packageVersion;
		}

		return versionText;
	}

	@Override
	public String toString()
	{
		String text = "";

		text += getVersionText();

		String displayName = getDisplayName();

		if(StringUtils.isNotEmpty(displayName))
		{
			text += " ";
			text += displayName;
		}

		return text;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Version version = (Version) o;

		if(majorVersion != version.majorVersion) return false;
		if(minorVersion != version.minorVersion) return false;
		if(patchVersion != version.patchVersion) return false;
		if(buildVersion != version.buildVersion) return false;
		if(packageVersion != version.packageVersion) return false;
		return displayName != null ? displayName.equals(version.displayName) : version.displayName == null;

	}

	@Override
	public int hashCode()
	{
		int result = majorVersion;
		result = 31 * result + minorVersion;
		result = 31 * result + patchVersion;
		result = 31 * result + buildVersion;
		result = 31 * result + packageVersion;
		result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
		return result;
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

	@Override
	public int compareTo(Version version)
	{
		if(version==null)
			throw new IllegalArgumentException("version must not be null");

		if(majorVersion > version.getMajorVersion())
			return 1;
		else if(majorVersion < version.getMajorVersion())
			return -1;
		else if(minorVersion > version.getMinorVersion())
			return 1;
		else if(minorVersion < version.getMinorVersion())
			return -1;
		else if(patchVersion > version.getPatchVersion())
			return 1;
		else if(patchVersion < version.getPatchVersion())
			return -1;
		else if(buildVersion > version.getBuildVersion())
			return 1;
		else if(buildVersion < version.getBuildVersion())
			return -1;
		else if(packageVersion > version.getPackageVersion())
			return 1;
		else if(packageVersion < version.getPackageVersion())
			return -1;
		else
			return 0;
	}
}
