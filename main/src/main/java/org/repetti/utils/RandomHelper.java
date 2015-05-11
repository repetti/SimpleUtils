package org.repetti.utils;

import java.util.Random;

/**
 * @author repetti
 */
public class RandomHelper {
    public static final Random r = new Random();
    private static final String letters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    public static String generateString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(r.nextInt(letters.length())));
        }
        return sb.toString();
    }
}
