package net.maatvirtue.commonlib.ffpdp;

import net.maatvirtue.commonlib.exception.FfpdpException;
import net.maatvirtue.commonlib.exception.InvalidTagFfpdpException;
import net.maatvirtue.commonlib.exception.NotImplementedFfpdpException;
import net.maatvirtue.commonlib.io.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class FfpdpUtil
{
	private static final int FFPDP_VERSION_NUM_BYTES = 2;
	private static final int FFPDP_V2_UID_NUM_BYTES = 4;
	private static final int FFPDP_V2_TYPE_NUM_BYTES = 2;
	private static final int FFPDP_V2_MAJOR_VERSION_NUM_BYTES = 2;
	private static final int FFPDP_V2_MINOR_VERSION_NUM_BYTES = 2;

	private static FfpdpUtil instance;

	private byte[] FFPDP_MAGIC;
	private byte[] SLASH;

	private FfpdpUtil()
	{
		try
		{
			FFPDP_MAGIC = "https://ffpdp.net/".getBytes("UTF-8");
			SLASH = "/".getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException exception)
		{
			throw new RuntimeException(exception); //Should not happen
		}
	}

	public static FfpdpUtil getInstance()
	{
		if(instance==null)
			instance = new FfpdpUtil();

		return instance;
	}

	public void writeFfpdpTag(OutputStream os, FfpdpTag ffpdpTag) throws IOException, FfpdpException
	{
		FfpdpVersion ffpdpVersion = ffpdpTag.getFfpdpVersion();

		if(ffpdpVersion == FfpdpVersion.V2)
			writeFfpdpTagV2(os, (FfpdpTagV2)ffpdpTag);
		else
			throw new NotImplementedFfpdpException("FFPDP Version "+ffpdpVersion+" not implemented");
	}

	public byte[] getFfpdpTagBytes(FfpdpTag ffpdpTag) throws IOException, FfpdpException
	{
		FfpdpVersion ffpdpVersion = ffpdpTag.getFfpdpVersion();

		if(ffpdpVersion == FfpdpVersion.V2)
			return getFfpdpTagV2Bytes((FfpdpTagV2)ffpdpTag);
		else
			throw new NotImplementedFfpdpException("FFPDP Version "+ffpdpVersion+" not implemented");
	}

	public FfpdpTag readFfpdpTag(byte[] ffpdpPrefixedData) throws IOException, FfpdpException
	{
		try(ByteArrayInputStream bais = new ByteArrayInputStream(ffpdpPrefixedData))
		{
			return readFfpdpTag(bais);
		}
	}

	public FfpdpTag readFfpdpTag(InputStream is) throws IOException, FfpdpException
	{
		byte[] magic = IoUtil.read(is, FFPDP_MAGIC.length);

		if(!Arrays.equals(magic, FFPDP_MAGIC))
			throw new InvalidTagFfpdpException("Invalid FFPDP Magic number");

		FfpdpVersion ffpdpVersion = readFfpdpVersion(is);

		if(ffpdpVersion == FfpdpVersion.V2)
			return readFfpdpTagV2(is);
		else
			throw new NotImplementedFfpdpException("FFPDP Version "+ffpdpVersion+" not implemented");
	}

	private byte[] getFfpdpTagV2Bytes(FfpdpTagV2 ffpdpTagV2) throws IOException
	{
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			writeFfpdpTagV2(baos, ffpdpTagV2);

			return baos.toByteArray();
		}
	}

	private void writeFfpdpTagV2(OutputStream os, FfpdpTagV2 ffpdpTagV2) throws IOException
	{
		if(!validateFfpdpTagV2Number(ffpdpTagV2.getUid(), FFPDP_V2_UID_NUM_BYTES, 1))
			throw new IllegalArgumentException("Invalid FFPDPV2 UID");

		if(!validateFfpdpTagV2Number(ffpdpTagV2.getType(), FFPDP_V2_TYPE_NUM_BYTES, 1))
			throw new IllegalArgumentException("Invalid FFPDPV2 type");

		if(!validateFfpdpTagV2Number(ffpdpTagV2.getMajorVersion(), FFPDP_V2_MAJOR_VERSION_NUM_BYTES, 0))
			throw new IllegalArgumentException("Invalid FFPDPV2 major version");

		if(!validateFfpdpTagV2Number(ffpdpTagV2.getMinorVersion(), FFPDP_V2_MINOR_VERSION_NUM_BYTES, 0))
			throw new IllegalArgumentException("Invalid FFPDPV2 minor version");

		os.write(FFPDP_MAGIC);
		os.write(getFfpdpEncodedNumber(FfpdpVersion.V2.getVersionNumber(), FFPDP_VERSION_NUM_BYTES));
		os.write(SLASH);
		os.write(getFfpdpEncodedNumber(ffpdpTagV2.getUid(), FFPDP_V2_UID_NUM_BYTES));
		os.write(SLASH);
		os.write(getFfpdpEncodedNumber(ffpdpTagV2.getType(), FFPDP_V2_TYPE_NUM_BYTES));
		os.write(SLASH);
		os.write(getFfpdpEncodedNumber(ffpdpTagV2.getMajorVersion(), FFPDP_V2_MAJOR_VERSION_NUM_BYTES));
		os.write(SLASH);
		os.write(getFfpdpEncodedNumber(ffpdpTagV2.getMinorVersion(), FFPDP_V2_MINOR_VERSION_NUM_BYTES));
	}

	private byte[] getFfpdpEncodedNumber(int number, int numberOfBytes)
	{
		String textnumber = Integer.toString(number);

		if(textnumber.length()>numberOfBytes)
			throw new IllegalArgumentException("number must fit in numberOfBytes");

		String paddedNumber = textnumber;

		for(int i=0; i<numberOfBytes-textnumber.length(); i++)
			paddedNumber = "0"+paddedNumber;

		try
		{
			return paddedNumber.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException exception)
		{
			throw new RuntimeException(exception); //Should not happen
		}
	}

	private boolean validateFfpdpTagV2Number(int number, int numberOfBytes, int minimumValue)
	{
		return number >= minimumValue && number <= (Math.pow(10, numberOfBytes) - 1);
	}

	private FfpdpTagV2 readFfpdpTagV2(InputStream is) throws IOException, InvalidTagFfpdpException
	{
		int uid;
		int type;
		int majorVersion;
		int minorVersion;

		readAndVerifyEquals(is, SLASH);
		uid = readFfpdpV2Uid(is);
		readAndVerifyEquals(is, SLASH);
		type = readFfpdpV2Type(is);
		readAndVerifyEquals(is, SLASH);
		majorVersion = readFfpdpV2MajorVersion(is);
		readAndVerifyEquals(is, SLASH);
		minorVersion = readFfpdpV2MinorVersion(is);

		return new FfpdpTagV2(uid, type, majorVersion, minorVersion);
	}

	private int readFfpdpV2Uid(InputStream is) throws InvalidTagFfpdpException, IOException
	{
		try
		{
			return readFfpdpEncodedNumber(IoUtil.read(is, FFPDP_V2_UID_NUM_BYTES));
		}
		catch(NumberFormatException exception)
		{
			throw new InvalidTagFfpdpException("Invalid FFPDPV2 UID");
		}
	}

	private int readFfpdpV2Type(InputStream is) throws InvalidTagFfpdpException, IOException
	{
		try
		{
			return readFfpdpEncodedNumber(IoUtil.read(is, FFPDP_V2_TYPE_NUM_BYTES));
		}
		catch(NumberFormatException exception)
		{
			throw new InvalidTagFfpdpException("Invalid FFPDPV2 type");
		}
	}

	private int readFfpdpV2MajorVersion(InputStream is) throws InvalidTagFfpdpException, IOException
	{
		try
		{
			return readFfpdpEncodedNumber(IoUtil.read(is, FFPDP_V2_MAJOR_VERSION_NUM_BYTES));
		}
		catch(NumberFormatException exception)
		{
			throw new InvalidTagFfpdpException("Invalid FFPDPV2 major version number");
		}
	}

	private int readFfpdpV2MinorVersion(InputStream is) throws InvalidTagFfpdpException, IOException
	{
		try
		{
			return readFfpdpEncodedNumber(IoUtil.read(is, FFPDP_V2_MINOR_VERSION_NUM_BYTES));
		}
		catch(NumberFormatException exception)
		{
			throw new InvalidTagFfpdpException("Invalid FFPDPV2 minor version number");
		}
	}

	private void readAndVerifyEquals(InputStream is, byte[] expected) throws IOException, InvalidTagFfpdpException
	{
		byte[] actual = IoUtil.read(is, expected.length);

		if(!Arrays.equals(actual, expected))
			throw new InvalidTagFfpdpException("Invalid FFPDP tag general syntax");
	}

	private FfpdpVersion readFfpdpVersion(InputStream is) throws IOException, InvalidTagFfpdpException
	{
		try
		{
			int ffpdpVersionNumber = readFfpdpEncodedNumber(IoUtil.read(is, 2));
			FfpdpVersion ffpdpVersion = FfpdpVersion.getByVersionNumber(ffpdpVersionNumber);

			return ffpdpVersion;
		}
		catch(IllegalArgumentException exception)
		{
			throw new InvalidTagFfpdpException("Invalid FFPDP version number", exception);
		}
	}

	private int readFfpdpEncodedNumber(byte[] number)
	{
		String textNumber = new String(number, Charset.forName("UTF-8"));

		return Integer.parseInt(textNumber);
	}
}
