package net.maatvirtue.commonlib.constants.packagemanager;

import net.maatvirtue.commonlib.service.ffpdp.FfpdpTagV2;

import java.util.regex.Pattern;

public class PackageManagerConstants
{
	public static final String ENVIRONMENT_COMPATIBILITY_SEPERATOR = ",";
	public static final Pattern PACKAGE_NAME_REGX = Pattern.compile("^[a-z]+[a-z0-9\\-]*$");
	public static FfpdpTagV2 CURRENT_PACKAGE_FFPDP_TAG = new FfpdpTagV2(2, 1, 1, 0);
	public static final String PACKAGE_RELATION_SEPERATOR = ",";
}
