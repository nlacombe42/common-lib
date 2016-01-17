package net.maatvirtue.commonlib.domain.packagemanager.pck;

public class EnvironmentCompatibility
{
	private static final String ENVIRONMENT_COMPATIBILITY_SEPERATOR = ":";

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

	public EnvironmentCompatibility(String environmentCompatibilityText)
	{
		environmentCompatibilityText = environmentCompatibilityText.trim();

		if(!environmentCompatibilityText.contains(ENVIRONMENT_COMPATIBILITY_SEPERATOR))
			throw new IllegalArgumentException("invalid environmentCompatibilityText");

		String[] environmentCompatibilityParts = environmentCompatibilityText.split(ENVIRONMENT_COMPATIBILITY_SEPERATOR);

		if(environmentCompatibilityParts.length!=3)
			throw new IllegalArgumentException("invalid environmentCompatibilityText");

		try
		{
			this.environmentType = EnvironmentType.getByCode(environmentCompatibilityParts[0].trim());
			this.environment = environmentCompatibilityParts[1].trim();
			this.compatibility = Compatibility.getByCode(environmentCompatibilityParts[2].trim());
		}
		catch(IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("invalid environmentCompatibilityText", exception);
		}
	}

	public String getEnvironmentCompatibilityText()
	{
		String environmentCompatibilityText = "";

		environmentCompatibilityText += environmentType.getCode();
		environmentCompatibilityText += ENVIRONMENT_COMPATIBILITY_SEPERATOR;
		environmentCompatibilityText += environment;
		environmentCompatibilityText += ENVIRONMENT_COMPATIBILITY_SEPERATOR;
		environmentCompatibilityText += compatibility.getCode();

		return environmentCompatibilityText;
	}

	@Override
	public String toString()
	{
		return getEnvironmentCompatibilityText();
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
