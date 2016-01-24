package net.maatvirtue.commonlib.domain.packagemanager.pck;

public enum VersionRelationType
{
	STRICTLY_EARLIER("<"),
	EARLIER_OR_EQUAL("<="),
	EQUAL("="),
	LATER_OR_EQUAL(">="),
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
