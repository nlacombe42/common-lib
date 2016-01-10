package net.maatvirtue.commonlib.domain.packagemanager;

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

	public static Compatibility getByCode(String code)
	{
		for(Compatibility compatibility: values())
			if(compatibility.getCode().equals(code))
				return compatibility;

		throw new IllegalArgumentException("no Compatibility for code: "+code);
	}

	public String getCode()
	{
		return code;
	}
}
