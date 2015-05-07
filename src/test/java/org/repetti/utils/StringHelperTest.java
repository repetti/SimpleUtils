package org.repetti.utils;

import org.junit.Ignore;
import org.junit.Test;

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

    @Ignore
    @Test
    public void testToHex() throws Exception {
        for (int i = 0; i < 10; i++) {
            long r = random.nextLong();
            byte[] b = new byte[8];
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.putLong(r);
//            System.out.println(StringHelper.toHexString(b));
//            System.out.println(Long.toHexString(r) + " / " + " = " + r + "\n");
            //TODO  will fail on first numbers starting with '0' ?
            assertEquals("now equal " + r, Long.toHexString(r), StringHelper.toHexString(b));
        }
    }

//    @Test
//    public void testToOct() throws Exception {
////        for (int i = 0; i < 10; i++) {
////            byte r = (byte) random.nextInt();
////            byte [] b = new byte[1];
////            ByteBuffer bb = ByteBuffer.wrap( b );
////            bb.put(r);
////            System.out.println(StringHelper.toOctString(b));
////            System.out.println(Long.toOctalString(r) + " / " + " = " + r + "\n");
////        }
//
//        for (int i = 0; i < 10; i++) {
//            int r = random.nextInt();
//            byte [] b = new byte[4];
//            ByteBuffer bb = ByteBuffer.wrap( b );
//            bb.putInt(r);
//            System.out.println(StringHelper.toEpamString(b));
//            System.out.println(String.format("%11s", Integer.toOctalString(r)) + " / " + " = " + r + "\n");
//        }
//
//        for (int i = 0; i < 10; i++) {
//            long r = random.nextLong();
//            byte [] b = new byte[8];
//            ByteBuffer bb = ByteBuffer.wrap( b );
//            bb.putLong(r);
//            System.out.println(StringHelper.toEpamString(b));
//            System.out.println(String.format("%22s", Long.toOctalString(r)) + " / " + " = " + r + "\n");
//        }
//    }
}
