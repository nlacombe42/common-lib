package net.maatvirtue.commonlib.domain.packagemanager;

public interface ApplicationSetupHandler
{
	void install();
	void upgrade();
	void uninstall();
}
