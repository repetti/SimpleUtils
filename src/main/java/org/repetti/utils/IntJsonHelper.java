package org.repetti.utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Date: 26/02/15
 *
 * @author repetti
 */
public class IntJsonHelper {

    public static String compareJson(JsonNode from, JsonNode to) {
        StringBuilder sb = new StringBuilder();
        compareJson(sb, "", from, to);
        return sb.toString();
    }

    public static void compareJson(StringBuilder sb, String prefix, JsonNode from, JsonNode to) {
        if (from == to) {
            sb.append(prefix).append("are equal");
            return;
        }
        if (from == null) {
            sb.append(prefix).append("added");
            return;
        }
        if (to == null) {
            sb.append(prefix).append("removed");
            return;
        }
        if (from.isObject() && to.isObject()) {
            sb.append(prefix).append("removed");
            return;
        }
    }

}
