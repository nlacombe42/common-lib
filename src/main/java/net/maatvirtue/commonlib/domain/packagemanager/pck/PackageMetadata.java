package net.maatvirtue.commonlib.domain.packagemanager.pck;

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

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		PackageMetadata that = (PackageMetadata) o;

		if(isVirtualPackage != that.isVirtualPackage) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(version != null ? !version.equals(that.version) : that.version != null) return false;
		if(packageAuthor != null ? !packageAuthor.equals(that.packageAuthor) : that.packageAuthor != null) return false;
		if(softwareAuthor != null ? !softwareAuthor.equals(that.softwareAuthor) : that.softwareAuthor != null)
			return false;
		if(environmentCompatibilities != null ? !environmentCompatibilities.equals(that.environmentCompatibilities) : that.environmentCompatibilities != null)
			return false;
		if(packageRelations != null ? !packageRelations.equals(that.packageRelations) : that.packageRelations != null)
			return false;
		return installationDataType == that.installationDataType;

	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (packageAuthor != null ? packageAuthor.hashCode() : 0);
		result = 31 * result + (softwareAuthor != null ? softwareAuthor.hashCode() : 0);
		result = 31 * result + (environmentCompatibilities != null ? environmentCompatibilities.hashCode() : 0);
		result = 31 * result + (isVirtualPackage ? 1 : 0);
		result = 31 * result + (packageRelations != null ? packageRelations.hashCode() : 0);
		result = 31 * result + (installationDataType != null ? installationDataType.hashCode() : 0);
		return result;
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
