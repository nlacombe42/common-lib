package net.maatvirtue.commonlib.packagemanager.pkg;

public class EnvironmentCompatibility
{
	private EnvironmentType environmentType;
	private Compatibility compatibility;
	private String environment;

	public EnvironmentCompatibility()
	{
		//Do nothing
	}

	public EnvironmentCompatibility(EnvironmentType environmentType, Compatibility compatibility, String environment)
	{
		this.environmentType = environmentType;
		this.compatibility = compatibility;
		this.environment = environment;
	}

	public EnvironmentType getEnvironmentType()
	{
		return environmentType;
	}

	public void setEnvironmentType(EnvironmentType environmentType)
	{
		this.environmentType = environmentType;
	}

	public Compatibility getCompatibility()
	{
		return compatibility;
	}

	public void setCompatibility(Compatibility compatibility)
	{
		this.compatibility = compatibility;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}
}
