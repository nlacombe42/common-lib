package net.maatvirtue.commonlib.ffpdp;

public class FfpdpTagV2 implements FfpdpTag
{
	private int uid;
	private int type;
	private int majorVersion;
	private int minorVersion;

	public FfpdpTagV2()
	{
		//Do nothing
	}

	public FfpdpTagV2(int uid, int type, int majorVersion, int minorVersion)
	{
		this.uid = uid;
		this.type = type;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
	}

	@Override
	public String toString()
	{
		return "FfpdpTagV2{"+
			"uid="+uid+
			", type="+type+
			", majorVersion="+majorVersion+
			", minorVersion="+minorVersion+
			'}';
	}

	@Override
	public FfpdpVersion getFfpdpVersion()
	{
		return FfpdpVersion.V2;
	}

	public int getUid()
	{
		return uid;
	}

	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
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
}
