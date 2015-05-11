package org.repetti.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author repetti
 */
public class ResourceHelperTests {
    public static final String TEXT = "test";
    public static final String FILE_RESOURCES = "atResource.txt";
    public static final String FILE_CURRENT = "src/test/resources/" + FILE_RESOURCES;

    @BeforeClass
    public static void beforeClass() {
        LoggerHelperSlf4j.setDebug();
    }

    @Test
    public void testReadFile() throws Exception {
        assertEquals(TEXT, ResourceHelper.readFile(FILE_RESOURCES));
        assertEquals(TEXT, ResourceHelper.readFile(FILE_CURRENT));
    }
}
