package org.repetti.utils;

/**
 * Class to manipulate slf4j logging levels
 * <p/>
 * see org.slf4j.impl.SimpleLogger
 *
 * @author repetti
 */
public class LoggerHelperSlf4j {

    /**
     * Sets SimpleLogger level to ERROR
     */
    public static void setError() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
    }

    /**
     * Sets SimpleLogger level to WARN
     */
    public static void setWarn() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
    }

    /**
     * Sets SimpleLogger level to INFO
     */
    public static void setInfo() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
    }

    /**
     * Sets SimpleLogger level to WARN
     */
    public static void setDebug() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    /**
     * Sets SimpleLogger level to TRACE
     */
    public static void setTrace() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    public static void setOutputFile(String filename) {
        System.setProperty("org.slf4j.simpleLogger.logFile", filename);
    }
}
