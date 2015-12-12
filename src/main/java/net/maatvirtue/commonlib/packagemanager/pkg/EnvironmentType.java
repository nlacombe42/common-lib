package net.maatvirtue.commonlib.packagemanager.pkg;

public enum EnvironmentType
{
	ARCHITECTURE("arch"),
	OS("os"),
	KERNEL("kernel"),
	DITRIBUTION("distro")
	;

	private String code;

	EnvironmentType(String code)
	{
		this.code = code;
	}
}
