package net.maatvirtue.commonlib.packagemanager.pkg;

import java.util.HashSet;
import java.util.Set;

public class PackageMetadata
{
	private String name;
	private Version version;
	private Contact packageAuthor;
	private Contact softwareAuthor;
	private Set<EnvironmentCompatibility> environmentCompatibilities;
	private boolean isVirtualPackage;
	private Set<PackageRelation> packageRelations;
	private InstallationDataType installationDataType;

	public PackageMetadata(String name, Version version)
	{
		this.name = name;
		this.version = version;
		this.environmentCompatibilities = new HashSet<>();
		this.isVirtualPackage = false;
		this.packageRelations = new HashSet<>();
		this.installationDataType = InstallationDataType.JAR;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Version getVersion()
	{
		return version;
	}

	public void setVersion(Version version)
	{
		this.version = version;
	}

	public Contact getPackageAuthor()
	{
		return packageAuthor;
	}

	public void setPackageAuthor(Contact packageAuthor)
	{
		this.packageAuthor = packageAuthor;
	}

	public Contact getSoftwareAuthor()
	{
		return softwareAuthor;
	}

	public void setSoftwareAuthor(Contact softwareAuthor)
	{
		this.softwareAuthor = softwareAuthor;
	}

	public Set<EnvironmentCompatibility> getEnvironmentCompatibilities()
	{
		return environmentCompatibilities;
	}

	public void setEnvironmentCompatibilities(Set<EnvironmentCompatibility> environmentCompatibilities)
	{
		this.environmentCompatibilities = environmentCompatibilities;
	}

	public void addEnvironmentCompatibility(EnvironmentCompatibility environmentCompatibility)
	{
		environmentCompatibilities.add(environmentCompatibility);
	}

	public boolean isVirtualPackage()
	{
		return isVirtualPackage;
	}

	public void setVirtualPackage(boolean virtualPackage)
	{
		isVirtualPackage = virtualPackage;
	}

	public Set<PackageRelation> getPackageRelations()
	{
		return packageRelations;
	}

	public void setPackageRelations(Set<PackageRelation> packageRelations)
	{
		this.packageRelations = packageRelations;
	}

	public void addPackageRelation(PackageRelation packageRelation)
	{
		packageRelations.add(packageRelation);
	}

	public InstallationDataType getInstallationDataType()
	{
		return installationDataType;
	}

	public void setInstallationDataType(InstallationDataType installationDataType)
	{
		this.installationDataType = installationDataType;
	}
}
