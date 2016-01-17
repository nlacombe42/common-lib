package net.maatvirtue.commonlib.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

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
		if(thread!=null&&thread.isAlive())
			thread.interrupt();
	}

	public static void interruptAndJoinThread(Thread thread)
	{
		interruptThreadIfRunning(thread);
		joinIgnoreInterrupts(thread);
	}

	public static String convertToHex(byte[] data)
	{
		StringBuilder buf = new StringBuilder();

		for(byte b : data)
		{
			int halfbyte = (b>>>4)&0x0F;
			int two_halfs = 0;
			do
			{
				buf.append((0<=halfbyte)&&(halfbyte<=9) ? (char)('0'+halfbyte) : (char)('a'+(halfbyte-10)));
				halfbyte = b&0x0F;
			} while(two_halfs++<1);
		}
		return buf.toString();
	}

	public static <T> boolean contains(T[] array, T value)
	{
		if(array==null)
			throw new IllegalArgumentException("array cannot be null");

		for(T element : array)
			if(value==element||(value!=null&&value.equals(element)))
				return true;

		return false;
	}

	/**
	 * Returns a random string of length len with possible chars: A-Za-z0-9
	 */
	public static String generateRandomString(int len)
	{
		char[] str = new char[len];
		int r;
		Random rand = new Random();

		for(int i = 0; i<len; i++)
		{
			r = rand.nextInt(62);

			if(r<26)
				str[i] = (char)('A'+r);
			else if(r<52)
				str[i] = (char)('a'+(r-26));
			else
				str[i] = (char)('0'+(r-52));
		}

		return new String(str);
	}
}
