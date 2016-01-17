package net.maatvirtue.commonlib.domain.packagemanager.pck;

import java.util.Arrays;
import java.util.List;

public class PackageRelation
{
	private static final String RELATION_TYPE_SEPERATOR = ":";

	private PackageRelationType relationType;
	private String packageName;
	private VersionRelationType versionRelationType;
	private Version packageVersion;

	public PackageRelation()
	{
		//Do nothing
	}

	public PackageRelation(PackageRelationType relationType, String packageName, VersionRelationType versionRelationType, Version packageVersion)
	{
		this.relationType = relationType;
		this.packageName = packageName;
		this.versionRelationType = versionRelationType;
		this.packageVersion = packageVersion;
	}

	public PackageRelation(String packageRelationText)
	{
		packageRelationText = packageRelationText.trim();

		if(!packageRelationText.contains(RELATION_TYPE_SEPERATOR))
			throw new IllegalArgumentException("invalid packageRelationText");

		String[] relationTypeParts = packageRelationText.split(RELATION_TYPE_SEPERATOR);

		if(relationTypeParts.length != 2)
			throw new IllegalArgumentException("invalid packageRelationText");

		try
		{
			this.relationType = PackageRelationType.getByCode(relationTypeParts[0]);

			parseAndSetRelation(relationTypeParts[1]);
		}
		catch(IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("invalid packageRelationText", exception);
		}
	}

	private void parseAndSetRelation(String relationText)
	{
		relationText = relationText.trim();

		VersionRelationType versionRelationType = findRelationType(relationText);

		if(versionRelationType == null)
			throw new IllegalArgumentException("invalid packageRelationText");

		this.versionRelationType = versionRelationType;

		String[] relationParts = relationText.split(versionRelationType.getCode());

		if(relationParts.length != 2)
			throw new IllegalArgumentException("invalid packageRelationText");

		this.packageName = relationParts[0].trim();

		try
		{
			this.packageVersion = new Version(relationParts[1].trim(), null);
		}
		catch(IllegalArgumentException exception)
		{
			throw new IllegalArgumentException("invalid packageRelationText", exception);
		}
	}

	private VersionRelationType findRelationType(String relationText)
	{
		List<VersionRelationType> orderedVersionRelationTypes =
				Arrays.asList(VersionRelationType.EARLIER, VersionRelationType.LATER, VersionRelationType.EQUAL,
						VersionRelationType.STRICTLY_EARLIER, VersionRelationType.STRICTLY_LATER);

		for(VersionRelationType versionRelationType: orderedVersionRelationTypes)
			if(relationText.contains(versionRelationType.getCode()))
				return versionRelationType;

		return null;
	}

	public String getPackageRelationText()
	{
		String packageRelationText = "";

		packageRelationText += relationType.getCode();
		packageRelationText += RELATION_TYPE_SEPERATOR;
		packageRelationText += packageName;
		packageRelationText += versionRelationType.getCode();
		packageRelationText += packageVersion.getVersionText();

		return packageRelationText;
	}

	@Override
	public String toString()
	{
		return getPackageRelationText();
	}

	public PackageRelationType getRelationType()
	{
		return relationType;
	}

	public void setRelationType(PackageRelationType relationType)
	{
		this.relationType = relationType;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public VersionRelationType getVersionRelationType()
	{
		return versionRelationType;
	}

	public void setVersionRelationType(VersionRelationType versionRelationType)
	{
		this.versionRelationType = versionRelationType;
	}

	public Version getPackageVersion()
	{
		return packageVersion;
	}

	public void setPackageVersion(Version packageVersion)
	{
		this.packageVersion = packageVersion;
	}
}
