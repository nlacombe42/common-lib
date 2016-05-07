package net.maatvirtue.commonlib.util.io;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class IoUtil
{
	public static int ubyte(byte b)
	{
		if(b >= 0)
			return b;
		else
			return b + 256;
	}

	public static int b4toint(byte[] buffer)
	{
		return ByteBuffer.wrap(buffer).getInt();
	}

	public static byte[] inttob4(int i)
	{
		return ByteBuffer.allocate(4).putInt(i).array();
	}

	public static void writeInteger(OutputStream os, int number) throws IOException
	{
		os.write(inttob4(number));
	}

	public static int readInteger(InputStream is) throws IOException
	{
		return b4toint(read(is, 4));
	}

	/**
	 * Read <code>size</code> bytes from input stream <code>is</code>.
	 * Keeps reading until all bytes are read or there is an error reading or reaches end of stream.
	 * If not all the bytes are read this methods throws an exception (even if end of stream is reached).
	 */
	public static byte[] read(InputStream is, int size) throws IOException
	{
		byte[] buffer = new byte[size];

		IOUtils.readFully(is, buffer);

		return buffer;
	}
}
