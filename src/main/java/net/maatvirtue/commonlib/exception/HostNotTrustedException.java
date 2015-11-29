package net.maatvirtue.commonlib.exception;

import java.security.PublicKey;

public class HostNotTrustedException extends Exception
{
	private PublicKey hostPublicKey;

	public HostNotTrustedException(PublicKey hostPublicKey)
	{
		this.hostPublicKey = hostPublicKey;
	}

	public HostNotTrustedException(String message)
	{
		super(message);
	}

	public HostNotTrustedException(Throwable cause)
	{
		super(cause);
	}

	public HostNotTrustedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PublicKey getHostPublicKey()
	{
		return hostPublicKey;
	}
}
