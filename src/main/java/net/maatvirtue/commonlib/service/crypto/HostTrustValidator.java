package net.maatvirtue.commonlib.service.crypto;

import java.security.PublicKey;

public interface HostTrustValidator
{
	boolean isTrustedHost(PublicKey publickey);
}
