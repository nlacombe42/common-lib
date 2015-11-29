package net.maatvirtue.commonlib.crypto;

import java.security.PublicKey;

public interface HostTrustValidator
{
	boolean isTrustedHost(PublicKey publickey);
}
