package org.repetti.utils;

/**
 * @author repetti
 */
public class UtilsConstants {

    public static final String BUILD_PACKAGE = "utils";
    /**
     * Build information
     */
    public static final String BUILD_PROPERTIES_FILENAME = "build.properties";

    public static final String BUILD_PARAM_PACKAGE = "package";
    public static final String BUILD_PARAM_VERSION = "build.version";
    public static final String BUILD_PARAM_NUMBER = "build.number";
    public static final String BUILD_PARAM_INFO = "build.info";
    public static final String BUILD_VALUE_NOT_DEFINED = "not set";

    public static final String MONGO_PRIMARY_KEY = "_id";

    /**
     * Data Source 'mongo' parameters
     */
    public static final String MONGO_HOST = "host";
    public static final String MONGO_PORT = "port";
    public static final String MONGO_HOSTS = "hosts";

    /**
     * Data Source 'mongo' authorisation parameters
     */
    public static final String MONGO_USE_SINGLE_HOST = "use.single.host";
    public static final String MONGO_AUTHORIZE = "authorize";
    public static final String MONGO_DATABASE_AUTH = "auth.db";
    public static final String MONGO_USERNAME = "auth.username";
    public static final String MONGO_PASSWORD = "auth.password";
    public static final String MONGO_DATABASE_DATA = "db.data";

    public static final String ZK_CONNECTION = "connection";
    public static final String ZK_TIMEOUT = "timeout";
    public static final String ZK_TIMEOUT_CONNECT = "timeout.connect";
    public static final String ZK_PATH_CONFIGURATION = "path.configuration";

    /**
     * Boolean value, true indicates absolute path
     */
    public static final String FILE_FULL = "full";

    /**
     * Filename string constant
     */
    public static final String FILE_NAME = "name";
}
