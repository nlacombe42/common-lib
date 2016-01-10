package net.maatvirtue.commonlib.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class IoUtil
{
	public static int ubyte(byte b)
	{
		if(b>=0)
			return b;
		else
			return b+256;
	}

	public static int b4toint(byte[] buffer)
	{
		return ByteBuffer.wrap(buffer).getInt();
	}

	public static byte[] inttob4(int i)
	{
		return ByteBuffer.allocate(4).putInt(i).array();
	}

	/**
	 * Read <code>size</code> bytes from input stream <code>is</code>.
	 * Keeps reading until all bytes are read or there is an error reading or reaches end of stream.
	 * If not all the bytes are read, this methods throws and exception (even if end of stream).
	 */
	public static byte[] read(InputStream is, int size) throws IOException
	{
		byte[] buffer = new byte[size];
		int totalBytesRead = 0;
		int bytesReadAtOnce;

		while(totalBytesRead<size)
		{
			bytesReadAtOnce = is.read(buffer, totalBytesRead, size-totalBytesRead);

			if(bytesReadAtOnce==-1)
				throw new IOException("Could not read enough bytes: end of stream.");

			totalBytesRead += bytesReadAtOnce;
		}

		return buffer;
	}
}
