package net.maatvirtue.commonlib.packagemanager.pkg;

public enum  PackageRelationType
{
	DEPENDS("depends"),
	PROVIDE("provide"),
	CONFLICTS("conflicts"),
	;

	private String code;

	PackageRelationType(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}
}
