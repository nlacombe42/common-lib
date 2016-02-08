package net.maatvirtue.commonlib.domain.packagemanager;

import net.maatvirtue.commonlib.constants.packagemanager.PackageManagerConstants;
import net.maatvirtue.commonlib.domain.daemon.Daemon;
import net.maatvirtue.commonlib.exception.UnkownCommandRuntimeException;
import net.maatvirtue.commonlib.service.packagemanager.ApplicationSetupRunner;
import net.maatvirtue.commonlib.util.GenericUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DaemonPackageRunner
{
	private static class ScriptDaemon implements Daemon
	{
		private String applicationName;

		public ScriptDaemon(String applicationName)
		{
			this.applicationName = applicationName;
		}

		@Override
		public void start()
		{
			executeCommand("start");
		}

		@Override
		public void stop()
		{
			executeCommand("stop");
		}

		@Override
		public boolean isRunning()
		{
			return false;
		}

		@Override
		public void restart()
		{
			executeCommand("restart");
		}

		private void executeCommand(String command)
		{
			try
			{
				Process process = Runtime.getRuntime().exec(new String[]{"./" + applicationName + ".sh", command});
				process.waitFor();

				if(process.exitValue() != 0)
					throw new RuntimeException("Error calling service script. Exit value not 0.");
			}
			catch(InterruptedException | IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	private static class ScriptDaemonSetupHandler extends DaemonSetupHandler
	{
		private String applicationName;

		public ScriptDaemonSetupHandler(String applicationName, Daemon daemon)
		{
			super(daemon);

			this.applicationName = applicationName;
		}

		@Override
		protected void doInstall()
		{
			try
			{
				InputStream scriptInputStream = getClass().getResourceAsStream("/" + PackageManagerConstants.DAEMON_SCRIPT_FILENAME);
				String script = IOUtils.toString(scriptInputStream);
				script = script.replaceAll("APP_NAME", applicationName);

				Path scriptFile = Paths.get(applicationName + ".sh");

				Files.write(scriptFile, script.getBytes());
				Files.setPosixFilePermissions(scriptFile, GenericUtil.getDefaultScriptPermissions());
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	public static void runCommand(String applicationName, String command, Runnable runnable)
	{
		ScriptDaemon scriptDaemon = new ScriptDaemon(applicationName);
		ScriptDaemonSetupHandler scriptDaemonSetupHandler = new ScriptDaemonSetupHandler(applicationName, scriptDaemon);

		try
		{
			ApplicationSetupRunner.runCommand(scriptDaemonSetupHandler, command);
			return;
		}
		catch(UnkownCommandRuntimeException exception)
		{
			//Continue execution
		}

		if(command.equals("run"))
			runnable.run();
		else
			throw new IllegalArgumentException("Unknown command: " + command);
	}
}
