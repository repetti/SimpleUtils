package org.repetti.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author repetti
 */
public class ExceptionHelper {
    /**
     * Prints stack trace to String
     *
     * @param e Throwable object
     * @return String with stack trace
     */
    public static String stackTraceToString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }
}
