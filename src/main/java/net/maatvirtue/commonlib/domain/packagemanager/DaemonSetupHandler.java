package net.maatvirtue.commonlib.domain.packagemanager;

import net.maatvirtue.commonlib.domain.daemon.Daemon;

public class DaemonSetupHandler implements ApplicationSetupHandler
{
	private Daemon daemon;

	public DaemonSetupHandler(Daemon daemon)
	{
		this.daemon = daemon;
	}

	@Override
	public final void install()
	{
		doInstall();
		daemon.start();
	}

	@Override
	public final void upgrade()
	{
		daemon.stop();
		doUpgrade();
		daemon.start();
	}

	@Override
	public final void uninstall()
	{
		daemon.stop();
		doUninstall();
	}

	protected void doInstall()
	{
		//Do Nothing
	}

	protected void doUpgrade()
	{
		//Do Nothing
	}

	protected void doUninstall()
	{
		//Do Nothing
	}
}
