package net.maatvirtue.commonlib.domain.packagemanager.pck;

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

	public static EnvironmentType getByCode(String code)
	{
		for(EnvironmentType environmentType: values())
			if(environmentType.getCode().equals(code))
				return environmentType;

		throw new IllegalArgumentException("no EnvironmentType with code: "+code);
	}

	public String getCode()
	{
		return code;
	}
}
