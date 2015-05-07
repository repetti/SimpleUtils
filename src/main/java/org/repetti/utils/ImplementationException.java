package org.repetti.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 16/02/15
 *
 * @author repetti
 */
public class ImplementationException extends RuntimeException {
    public static final Logger log = LoggerFactory.getLogger(ImplementationException.class);

    public ImplementationException(String message) {
        super(message);
    }

    public static void err(String message) throws ImplementationException {
        ImplementationException e = new ImplementationException(message);
        e.printStackTrace(System.err);
        throw e;
    }

    public static void out(String message) throws ImplementationException {
        ImplementationException e = new ImplementationException(message);
        e.printStackTrace(System.out);
        throw e;
    }

    public static void log(String message) throws ImplementationException {
        ImplementationException e = new ImplementationException(message);
        log.error(message, e);
        throw e;
    }
}
