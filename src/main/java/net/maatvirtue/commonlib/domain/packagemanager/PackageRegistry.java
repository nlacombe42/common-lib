package net.maatvirtue.commonlib.domain.packagemanager;

import net.maatvirtue.commonlib.domain.packagemanager.pck.PackageMetadata;

import java.util.Map;
import java.util.Set;

public class PackageRegistry
{
	private Map<String, PackageMetadata> registry;

	public PackageRegistry(Map<String, PackageMetadata> registry)
	{
		this.registry = registry;
	}

	public Set<String> getPackageNames()
	{
		return registry.keySet();
	}

	public PackageMetadata getPackageMetadata(String packageName)
	{
		return registry.get(packageName);
	}

	public boolean isPackageInRegistry(String packageName)
	{
		return registry.containsKey(packageName);
	}

	public int getNumberOfPackages()
	{
		return registry.size();
	}

	public void addPackageToRegistry(PackageMetadata packageMetadata)
	{
		if(isPackageInRegistry(packageMetadata.getName()))
			throw new IllegalArgumentException("Package "+packageMetadata.getName()+" already exists in registry");

		registry.put(packageMetadata.getName(), packageMetadata);
	}

	public void removePackage(String packageName)
	{
		if(!isPackageInRegistry(packageName))
			throw new IllegalArgumentException("Package "+packageName+" not in registry");

		registry.remove(packageName);
	}
}
