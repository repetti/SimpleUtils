package org.repetti.utils.jar;

/**
 * The only exception that can be thrown
 *
 * @author repetti
 */
public class JarException extends Exception {
    public JarException(String message, Exception cause) {
        super(message, cause);
    }
}
