package org.repetti.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created on 16/05/15.
 */
public class SecurityHelper {
    private static final char[] CHARS = (
            "QWERTYUIOPASDFGHJKLZXCVBNM" + "qwertyuiopasdfghjklzxcvbnm" +
                    "0123456789,./<>?;':\"[]{}!@#$%^&*()_+-=\\").toCharArray();

    /**
     * 0 looks like O
     * 1 looks like l
     */
    private static final String[] VOWELS = {
            "a", "A",
            "e", "E",
            "y", "Y",
            "u", "U",
            "i", "I",
            "o", "O",
            "3",
    };
    /**
     *
     */
    private static final String[] CONSONANTS = {
            "q", "Q",
            "w", "W",
            "r", "R",
            "t", "T",
            "p", "P",
            "s", "S",
            "d", "D",
            "f", "F",
            "g", "G",
            "h", "H",
            "j", "J",
            "k", "K",
            "l", "L",
            "z", "Z",
            "x", "X",
            "c", "C",
            "v", "V",
            "b", "B",
            "n", "N",
            "m", "M",
            "2",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
    };
    private static final String[] SYMBOLS = {
            "!",
            "@",
            "#",
            "$",
            "%",
            "^",
            "&",
            "*",
            "-",
            "+",
            "/",
            "{",
            "}",
            "[",
            "]",
            ";",
            ":",
            ",",
            ".",
            "<",
            ">",
    };
    private static final Random r = new SecureRandom();

    /**
     * Generates random string from predefined set of chars
     *
     * @param length length of the resulting string
     * @return generated string
     * @throws IllegalArgumentException if desired length is not positive
     */
    public static String generatePassword(int length) {
        return generatePassword(length, CHARS);
    }

    /**
     * Generates random string from predefined set of chars
     *
     * @param length length of the resulting string
     * @param chars  set of chars to use
     * @return generated string
     * @throws IllegalArgumentException
     */
    public static String generatePassword(final int length, char[] chars) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length should be positive");
        }
        if (chars.length == 0) {
            throw new IllegalArgumentException("Length should be positive");
        }
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = CHARS[r.nextInt(CHARS.length)];
        }
        return String.valueOf(ret);
    }

    private static String getConsonant() {
        return CONSONANTS[r.nextInt(CONSONANTS.length)];
    }

    private static String getVowel() {
        return VOWELS[r.nextInt(VOWELS.length)];
    }

    private static String getSymbol() {
        return SYMBOLS[r.nextInt(SYMBOLS.length)];
    }

    /**
     * Generates N words.
     * vc, cvc, ccv, cvv (cvs)
     *
     * @param wordLengths lengths of words to generate in morphemes
     * @return string, containing of words generated
     */
    public static String generateReadablePassword(final int... wordLengths) {
        if (wordLengths.length == 0) {
            throw new IllegalArgumentException("At least one word to be generated");
        }
        StringBuilder sb = new StringBuilder();

        for (int w = 0; w < wordLengths.length; w++) {
            if (wordLengths[w] <= 0) {
                throw new IllegalArgumentException("Length should be positive");
            }
            if (w > 0) {
                sb.append(' ');
            }
            for (int s = 0; s < wordLengths[w]; s++) {
                switch (r.nextInt(5)) {
                    case 0:
                        sb.append(getVowel())
                                .append(getConsonant());
                        break;
                    case 1:
                        sb.append(getConsonant())
                                .append(getVowel())
                                .append(getConsonant());
                        break;
                    case 2:
                        sb.append(getConsonant())
                                .append(getConsonant())
                                .append(getVowel());
                        break;
                    case 3:
                        sb.append(getConsonant())
                                .append(getVowel())
                                .append(getVowel());
                        break;
                    case 4:
                        sb.append(getConsonant())
                                .append(getVowel())
                                .append(getSymbol());
                        break;
                    default:
                        throw new RuntimeException("Error in implementation");
                }
            }
            sb.append(getSymbol());
        }
        return sb.toString();
    }
}
