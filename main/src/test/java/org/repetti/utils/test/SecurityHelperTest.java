package org.repetti.utils.test;

import org.junit.Test;
import org.repetti.utils.LoggerHelperSlf4j;
import org.repetti.utils.ResourceHelper;
import org.repetti.utils.SecurityHelper;
import org.repetti.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Date: 15/07/15
 */
public class SecurityHelperTest {
    private static final Logger log = LoggerFactory.getLogger(SecurityHelperTest.class);

    static {
        LoggerHelperSlf4j.setDebug();
    }

    @Test
    public void testSha256() throws Exception {
        File f = new File(ResourceHelperTests.FILE_CURRENT);
        String s = ResourceHelper.readFile(ResourceHelperTests.FILE_RESOURCES);
        log.debug("filename: {}\noriginal text: {}", f.getAbsolutePath(), s);

        final String shaFile = StringHelper.toHexString(SecurityHelper.sha256(f));
        final String shaBytes = StringHelper.toHexString(SecurityHelper.sha256(s.getBytes()));
        log.debug("shaFile:  {}\nshaBytes: {}", shaFile, shaBytes);

        assertEquals(shaFile, shaBytes);
    }
}
