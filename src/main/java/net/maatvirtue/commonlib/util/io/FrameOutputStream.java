package net.maatvirtue.commonlib.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FrameOutputStream extends FilterOutputStream
{
	private int sizeFieldLength;

	public FrameOutputStream(OutputStream os)
	{
		this(os, 4);
	}

	public FrameOutputStream(OutputStream os, int sizeFieldLength)
	{
		super(os);

		if(!(sizeFieldLength==1||sizeFieldLength==2||sizeFieldLength==4))
			throw new IllegalArgumentException("sizeFieldLength can only be 1 or 2 or 4 bytes");

		this.sizeFieldLength=sizeFieldLength;
	}

	private void writeFrameSize(int frameSize) throws IOException
	{
		if(frameSize>(Math.pow(256, sizeFieldLength)))
			throw new IllegalArgumentException("frameSize must fit in sizeFieldLength (frameSize: "+frameSize+", sizeFieldLength: "+sizeFieldLength+")");

		if(sizeFieldLength==1)
		{
			write((byte)frameSize);
		}
		else if(sizeFieldLength==2)
		{
			write(ByteBuffer.allocate(sizeFieldLength).putShort((short)frameSize).array());
		}
		else
		{
			write(ByteBuffer.allocate(sizeFieldLength).putInt(frameSize).array());
		}
	}

	public void writeFrame(byte[] frame) throws IOException
	{
		if(frame==null)
			throw new IllegalArgumentException("frame cannot be null");

		writeFrameSize(frame.length);
		write(frame);
	}
}
