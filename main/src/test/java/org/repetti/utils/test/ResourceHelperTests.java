package org.repetti.utils.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.repetti.utils.LoggerHelperSlf4j;
import org.repetti.utils.ResourceHelper;

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
        Assert.assertEquals(TEXT, ResourceHelper.readFile(FILE_RESOURCES));
        assertEquals(TEXT, ResourceHelper.readFile(FILE_CURRENT));
    }
}
