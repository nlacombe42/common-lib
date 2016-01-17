package net.maatvirtue.commonlib.constants.packagemanager;

import net.maatvirtue.commonlib.service.ffpdp.FfpdpTagV2;

import java.util.regex.Pattern;

public class PackageManagerConstants
{
	public static final String ENVIRONMENT_COMPATIBILITY_SEPERATOR = ",";
	public static final String PACKAGE_RELATION_SEPERATOR = ",";

	public static final Pattern PACKAGE_NAME_REGX = Pattern.compile("^[a-z]+[a-z0-9\\-]*$");

	public static final FfpdpTagV2 CURRENT_PACKAGE_FFPDP_TAG = new FfpdpTagV2(2, 1, 1, 0);
	public static final FfpdpTagV2 CURRENT_REGISTRY_FFPDP_TAG = new FfpdpTagV2(2, 2, 1, 0);

	public static final String PACKAGE_MANAGER_FOLDER_NAME = "pckmgr";
	public static final String REGISTRY_FILE_NAME = "registry";
	public static final String LOCKFILE_FILE_NAME = "lock";
	public static final String PACKAGE_MANAGER_ROOT_SIGNING_PUBLIC_KEY_FILENAME = "pckmgr-root-signing-public.pem";
}
