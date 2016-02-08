package net.maatvirtue.commonlib.constants.packagemanager;

import net.maatvirtue.commonlib.service.ffpdp.FfpdpTagV2;
import net.maatvirtue.commonlib.util.GenericUtil;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class PackageManagerConstants
{
	public static final String ENVIRONMENT_COMPATIBILITY_SEPERATOR = ",";
	public static final String PACKAGE_RELATION_SEPERATOR = ",";

	public static final Pattern PACKAGE_NAME_REGX = Pattern.compile("^[a-z]+[a-z0-9\\-]*$");

	public static final FfpdpTagV2 CURRENT_PACKAGE_FFPDP_TAG = new FfpdpTagV2(2, 1, 1, 0);
	public static final FfpdpTagV2 CURRENT_REGISTRY_FFPDP_TAG = new FfpdpTagV2(2, 2, 1, 0);

	public static final String PACKAGE_MANAGER_ROOT_SIGNING_PUBLIC_KEY_FILENAME = "pckmgr-root-signing-public.pem";

	public static final Path PACKAGE_MANAGER_FOLDER = GenericUtil.getUserHomeFolder().resolve(".pckmgr");
	public static final Path LOCK_FILE = PACKAGE_MANAGER_FOLDER.resolve("lock");
	public static final Path REGISTRY_FILE = PACKAGE_MANAGER_FOLDER.resolve("registry");

	public static final String APPLICATION_INSTALL_COMMAND = "install";
	public static final String APPLICATION_UPGRADE_COMMAND = "upgrade";
	public static final String APPLICATION_UNINSTALL_COMMAND = "uninstall";
}
