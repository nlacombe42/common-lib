package net.maatvirtue.commonlib.ffpdp;

public enum FfpdpVersion
{
	V2(2),
	RESERVED(99);

	private int versionNumber;

	FfpdpVersion(int versionNumber)
	{
		this.versionNumber = versionNumber;
	}

	public static FfpdpVersion getByVersionNumber(int versionNumber)
	{
		for(FfpdpVersion ffpdpVersion: values())
			if(ffpdpVersion.getVersionNumber()==versionNumber)
				return ffpdpVersion;

		throw new IllegalArgumentException("No FfpdpVersion for version number "+versionNumber);
	}

	public int getVersionNumber()
	{
		return versionNumber;
	}
}
