package org.repetti.utils.task;

import com.fasterxml.jackson.databind.JsonNode;
import org.repetti.utils.JsonHelper;
import org.repetti.utils.ResourceHelper;
import org.repetti.utils.UtilsException;

import java.io.File;

/**
 * Read the json file and write it back
 * <p/>
 * Date: 20/05/15
 */
public class JsonFormatTask {
    public static void main(String[] args) throws UtilsException {
        File f = new File("main/src/test/resources/data.json");
//        String s = ResourceHelper.readFileAsString(f);
        JsonNode j = JsonHelper.parse(f);
        String res = JsonHelper.printFormattedSorted(j);
        System.out.println(res);

        ResourceHelper.writeFile(f, res);
    }
}
