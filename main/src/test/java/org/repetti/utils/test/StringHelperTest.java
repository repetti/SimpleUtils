package org.repetti.utils.test;

import org.junit.Test;
import org.repetti.utils.StringHelper;

import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Date: 23/04/15
 *
 * @author repetti
 */
public class StringHelperTest {
    private static final Random random = new Random();
    private static final String[] zeros = {
            "",
            "0",
            "00",
            "000",
            "0000",
            "00000",
            "000000",
            "0000000",
            "00000000",
            "000000000",
            "0000000000",
            "00000000000",
            "000000000000",
            "0000000000000",
            "00000000000000",
            "000000000000000",
    };

    /**
     * Compares with framework toHexString method from Long class
     */
    @Test
    public void testToHex() {
        for (int i = 0; i < 20; i++) {
            long r = random.nextLong();
            byte[] b = new byte[8];
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.putLong(r);
            String expected = zeroExpand(Long.toHexString(r), 16);
            final String actual = StringHelper.toHexString(b);
//            System.out.println(expected + "\n" + actual);
            assertEquals("now equal " + r, expected, actual);
        }
    }

    private String zeroExpand(String original, int length) {

        if (original.length() == length) {
            return original;
        }
        int diff = length - original.length();
        if (diff < zeros.length) {
            return zeros[diff] + original;
        } else {
            // no need to optimize now
            while (diff >= zeros.length) {
                diff -= zeros.length - 1;
                original = zeros[zeros.length - 1] + original;
            }
            return zeros[diff] + original;
        }
    }

    @Test
    public void testToOct() {
        byte[] bytes;// = new byte[1];
//        for (int i = 0; i < 20; i++) {
//            random.nextBytes(bytes);
//            final String actual = StringHelper.toOctString(bytes);
//            final String expected = zeroExpand(Byte.toOctalString(bytes[0]), actual.length());
//            assertEquals("now equal " + bytes[0], expected, actual);
//        }
        bytes = new byte[4];
        for (int i = 0; i < 20; i++) {
            int r = random.nextInt();
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            bb.putInt(r);
            final String actual = StringHelper.toOctString(bytes);
            final String expected = zeroExpand(Integer.toOctalString(r), actual.length());
            assertEquals("now equal " + r, expected, actual);
            System.out.println(actual + "\n" + expected + " = " + r + "\n");
        }
        bytes = new byte[8];
        for (int i = 0; i < 20; i++) {
            long r = random.nextLong();
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            bb.putLong(r);
            final String actual = StringHelper.toOctString(bytes);
            final String expected = zeroExpand(Long.toOctalString(r), actual.length());
            assertEquals("now equal " + r, expected, actual);
            System.out.println(actual + "\n" + expected + " = " + r + "\n");
        }
    }
}
