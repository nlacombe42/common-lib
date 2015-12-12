package net.maatvirtue.commonlib.packagemanager.pkg;

public enum Compatibility
{
	COMPATIBLE("compatible"),
	INCOMPATIBLE("incompatible")
	;

	private String code;

	Compatibility(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}
}
