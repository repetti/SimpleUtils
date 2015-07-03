package org.repetti.utils;

/**
 * The only exception to be thrown by utils.
 *
 * @author repetti
 */
public class UtilsException extends Exception {

    public final Type type;

    public UtilsException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public enum Type {
        NOT_FOUND,
        PARAMETERS,
        PARSING,
        CONNECTION,
        FORBIDDEN,
        UNDEFINED;

        public UtilsException build(String message, Throwable cause) {
            return new UtilsException(this, message, cause);
        }
    }

//    public static UtilsException notFound(String message, Exception cause) {
//        return new UtilsException(Type.NOT_FOUND, message, cause);
//    }
//
//    public static UtilsException badParameters(String message, Exception cause) {
//        return new UtilsException(Type.PARAMETERS, message, cause);
//    }
}
