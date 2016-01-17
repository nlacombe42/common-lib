package net.maatvirtue.commonlib.domain.packagemanager.pck;

public enum VersionRelationType
{
	STRICTLY_EARLIER("<"),
	EARLIER("<="),
	EQUAL("="),
	LATER(">="),
	STRICTLY_LATER(">")
	;

	private String code;

	VersionRelationType(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}
}
