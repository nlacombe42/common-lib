package net.maatvirtue.commonlib.packagemanager.pkg;

public enum InstallationDataType
{
	JAR("jar")
	;

	private String code;

	InstallationDataType(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}
}
