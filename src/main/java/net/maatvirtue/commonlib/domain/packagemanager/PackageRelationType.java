package net.maatvirtue.commonlib.domain.packagemanager;

public enum PackageRelationType
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

	public static PackageRelationType getByCode(String code)
	{
		for(PackageRelationType packageRelationType: values())
			if(packageRelationType.getCode().equals(code))
				return packageRelationType;

		throw new IllegalArgumentException("invalid PackageRelationType code: "+code);
	}

	public String getCode()
	{
		return code;
	}
}
