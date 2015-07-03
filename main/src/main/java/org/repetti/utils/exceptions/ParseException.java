package org.repetti.utils.exceptions;

import org.repetti.utils.UtilsException;

/**
 * Date: 16/06/15
 */
public class ParseException extends UtilsException {

    public ParseException(String message) {
        this(message, null);
    }

    public ParseException(String message, Throwable cause) {
        super(Type.PARSING, message, cause);
    }
}
