package net.maatvirtue.commonlib.domain.packagemanager;

public enum InstallationDataType
{
	JAR("jar")
	;

	private String code;

	InstallationDataType(String code)
	{
		this.code = code;
	}

	public static InstallationDataType getByCode(String code)
	{
		for(InstallationDataType installationDataType: values())
			if(installationDataType.getCode().equals(code))
				return installationDataType;

		throw new IllegalArgumentException("Invalid InstallationDataType code: "+code);
	}

	public String getCode()
	{
		return code;
	}
}
