package net.maatvirtue.commonlib.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GenericUtil
{
	public static void joinIgnoreInterrupts(Thread thread)
	{
		while(true)
		{
			try
			{
				thread.join();
				return;
			}
			catch(InterruptedException e)
			{
				//ignoring interrupt
			}
		}
	}

	public static Path getUserHomeFolder()
	{
		return Paths.get(System.getProperty("user.home"));
	}

	public static void interruptThreadIfRunning(Thread thread)
	{
		if(thread != null && thread.isAlive())
			thread.interrupt();
	}

	public static void interruptAndJoinThread(Thread thread)
	{
		interruptThreadIfRunning(thread);
		joinIgnoreInterrupts(thread);
	}

	public static Set<PosixFilePermission> getDefaultScriptPermissions()
	{
		Set<PosixFilePermission> permissions = new HashSet<>();

		permissions.add(PosixFilePermission.OWNER_READ);
		permissions.add(PosixFilePermission.OWNER_WRITE);
		permissions.add(PosixFilePermission.OWNER_EXECUTE);
		permissions.add(PosixFilePermission.GROUP_READ);
		permissions.add(PosixFilePermission.GROUP_EXECUTE);
		permissions.add(PosixFilePermission.OTHERS_READ);
		permissions.add(PosixFilePermission.OTHERS_EXECUTE);

		return permissions;
	}

	/**
	 * Returns a random string of length len with possible chars: A-Za-z0-9
	 */
	public static String generateRandomString(int len)
	{
		char[] str = new char[len];
		int r;
		Random rand = new Random();

		for(int i = 0; i < len; i++)
		{
			r = rand.nextInt(62);

			if(r < 26)
				str[i] = (char) ('A' + r);
			else if(r < 52)
				str[i] = (char) ('a' + (r - 26));
			else
				str[i] = (char) ('0' + (r - 52));
		}

		return new String(str);
	}
}
