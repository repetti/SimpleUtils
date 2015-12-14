package org.repetti.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.jetbrains.annotations.Nullable;
import org.repetti.utils.exceptions.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author repetti
 */
public class JsonHelper {

    public final static ObjectMapper mapper = new ObjectMapper();
    protected final static Logger log = LoggerFactory.getLogger(JsonHelper.class);

    public static ObjectNode newObjectNode(String key, JsonNode value) {
        ObjectNode ret = JsonHelper.mapper.createObjectNode();
        ret.set(key, value);
        return ret;
    }

    public static boolean contains(ArrayNode arrayNode, String value) {
        for (JsonNode j : arrayNode) {
            if (value.equals(j.textValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that node is an instance of ArrayNode
     *
     * @return null => null
     * @throws UtilsException if not an ArrayNode
     */
    public static ArrayNode castToArrayNode(JsonNode node) throws UtilsException {
        if (node == null) {
            return null;
        }
        if (node.isArray()) {
            return (ArrayNode) node;
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, node + " is not an array node", null);
    }

    /**
     * Checks that node is an instance of ObjectNode
     *
     * @return null => null
     * @throws UtilsException if not an ObjectNode
     */
    public static ObjectNode castToObjectNode(JsonNode node) throws UtilsException {
        if (node == null) {
            return null;
        }
        if (node.isObject()) {
            return (ObjectNode) node;
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, node + " is not an object node", null);
    }

    public static List<String> convertToList(@Nullable ArrayNode arrayNode) {
        if (arrayNode == null) {
            return null;
        }
        List<String> ret = new ArrayList<String>();
        for (JsonNode j : arrayNode) {
            ret.add(j.asText());
        }
        return ret;
    }

    public static ArrayNode convertToArrayNode(@Nullable Collection<String> list) {
        if (list == null) {
            return null;
        }
        ArrayNode ret = newArrayNode();
        for (String e : list) {
            ret.add(e);
        }
        return ret;
    }

    public static ArrayNode newArrayNode() {
        return JsonHelper.mapper.createArrayNode();
    }

    public static ArrayNode convertToArrayNode(String... list) {
        if (list == null) {
            return null;
        }
        ArrayNode ret = newArrayNode();
        for (String e : list) {
            ret.add(e);
        }
        return ret;
    }

    public static JsonNode parse(InputStream inputStream) throws UtilsException {
        try {
            return mapper.readTree(inputStream);
        } catch (Exception e) {
            throw new UtilsException(UtilsException.Type.CONNECTION, "error reading IO stream: " + inputStream, e);
        }
    }

    public static JsonNode parse(File file) throws UtilsException {
        try {
            return mapper.readTree(file);
        } catch (Exception e) {
            throw new UtilsException(UtilsException.Type.CONNECTION, "error reading file: " + file, e);
        }
    }

    public static JsonNode parse(String text) throws UtilsException {
        try {
            return mapper.readTree(text);
        } catch (IOException e) {
            throw new UtilsException(UtilsException.Type.CONNECTION, "error parsing text: " + text, e);
        }
    }

    /**
     * Transforms string to JSON representation of it. That means that some characters will be "shielded".
     * Quotation is added.
     */
    public static String jsonify(String text) {
//        try {
        return new TextNode(text).toString();
//        } catch (IOException e) {
//            throw new UtilsException(UtilsException.Type.CONNECTION, "error parsing text: " + text, e);
//        }
    }

    public static String serialize(JsonNode j) {
        return j.toString();
    }

    public static String toPrintable(JsonNode j) {
        return j == null ? null : j.asText();
    }

    public static String printFormattedSorted(JsonNode j) {
        StringBuilder sb = new StringBuilder();
        printFormattedSorted(j, sb, 0, 2);
        return sb.toString();
    }

    public static String printFormatted(JsonNode j) {
        StringBuilder sb = new StringBuilder();
        printFormatted(j, sb, 0, 2);
        return sb.toString();
    }

    public static void printFormatted(JsonNode j, StringBuilder sb, int spaces, int tabSize) {
        if (j.isArray()) {
            sb.append("[");
            boolean notEmpty = false;
            for (JsonNode t : j) {
                if (notEmpty) {
                    sb.append(",\n");
                } else {
                    notEmpty = true;
                    sb.append("\n");
                }
                for (int i = 0; i < spaces + tabSize; i++) {
                    sb.append(' ');
                }
                printFormatted(t, sb, spaces + tabSize, 2);

            }
            if (notEmpty) {
                sb.append('\n');
                for (int i = 0; i < spaces; i++) {
                    sb.append(' ');
                }
            }
            sb.append("]");
        } else if (j.isObject()) {
            ObjectNode o = (ObjectNode) j;
            sb.append("{");
            boolean notEmpty = false;
            Iterator<Map.Entry<String, JsonNode>> i = o.fields();
            while (i.hasNext()) {
                Map.Entry<String, JsonNode> cur = i.next();
                JsonNode t = cur.getValue();
                if (notEmpty) {
                    sb.append(",\n");
                } else {
                    notEmpty = true;
                    sb.append("\n");
                }
                for (int tmp = 0; tmp < spaces + tabSize; tmp++) {
                    sb.append(' ');
                }
                sb.append(jsonify(cur.getKey())).append(": ");
                printFormatted(t, sb, spaces + tabSize, 2);

            }
            if (notEmpty) {
                sb.append('\n');
                for (int tmp = 0; tmp < spaces; tmp++) {
                    sb.append(' ');
                }
            }
            sb.append("}");
        } else {
            sb.append(j.toString());
        }
    }

    public static void printFormattedSorted(JsonNode j, StringBuilder sb, int spaces, int tabSize) {
        if (j.isArray()) {
            sb.append("[");
            boolean notEmpty = false;
            for (JsonNode t : j) {
                if (notEmpty) {
                    sb.append(",\n");
                } else {
                    notEmpty = true;
                    sb.append("\n");
                }
                for (int i = 0; i < spaces + tabSize; i++) {
                    sb.append(' ');
                }
                printFormattedSorted(t, sb, spaces + tabSize, 2);

            }
            if (notEmpty) {
                sb.append('\n');
                for (int i = 0; i < spaces; i++) {
                    sb.append(' ');
                }
            }
            sb.append("]");
        } else if (j.isObject()) {
            ObjectNode o = (ObjectNode) j;
            sb.append("{");
            boolean notEmpty = false;
            Iterator<String> nameIterator = o.fieldNames();
            TreeSet<String> fields = new TreeSet<String>();
            while (nameIterator.hasNext()) {
                fields.add(nameIterator.next());
            }
            for (String cur : fields) {
                JsonNode t = o.get(cur);
                if (notEmpty) {
                    sb.append(",\n");
                } else {
                    notEmpty = true;
                    sb.append("\n");
                }
                for (int tmp = 0; tmp < spaces + tabSize; tmp++) {
                    sb.append(' ');
                }
                sb.append(jsonify(cur)).append(": ");
                printFormattedSorted(t, sb, spaces + tabSize, 2);

            }
            if (notEmpty) {
                sb.append('\n');
                for (int tmp = 0; tmp < spaces; tmp++) {
                    sb.append(' ');
                }
            }
            sb.append("}");
        } else {
            sb.append(j.toString());
        }
    }

    /**
     * @param throwOnEmptyPath will throw an exception if path doesn't exist in from object
     */
    public static void replaceNode(JsonNode from, JsonNode to, boolean throwOnEmptyPath, String... path) throws UtilsException {
        if (path == null || path.length < 1) {
            throw new UtilsException(UtilsException.Type.PARAMETERS, "path is too short", null);
        }
        final int length = path.length;
        JsonNode f = from, t = to;
        for (int i = 0; i < length - 1; i++) {
            final String p = path[i];
            if (f.has(p)) {
                f = f.get(p);
            } else {
                if (throwOnEmptyPath) {
                    throw new UtilsException(UtilsException.Type.PARAMETERS, "path doesn't exist", null);
                } else {
                    return;
                }
            }
            if (t.has(p)) {
                t = t.get(p);
            } else {
                ((ObjectNode) t).set(p, newObjectNode());
            }
        }
        final String p = path[length - 1];
        if (f.has(p)) {
            f = f.get(p);
        } else {
            if (throwOnEmptyPath) {
                throw new UtilsException(UtilsException.Type.PARAMETERS, "path doesn't exist: " + Arrays.toString(path), null);
            } else {
                return;
            }
        }
        ((ObjectNode) t).set(p, f);
    }

//    public static void replaceNode(JsonNode from, JsonNode to, List<String> path) {
//
//    }

    public static ObjectNode newObjectNode() {
        return JsonHelper.mapper.createObjectNode();
    }

    public static String getString(ObjectNode o, String field, String defaultValue) {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            if (ret.isTextual()) {
                return ret.asText();
            }
        }
        return defaultValue;
    }

    public static String getString(ObjectNode o, String field) throws ParseException, UtilsException {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            if (ret.isTextual()) {
                return ret.asText();
            }
            throw new ParseException(field + " is not textual");
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, field + " not found", null);
    }

    public static JsonNode getJson(ObjectNode o, String field, JsonNode defaultValue) {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            return ret;
        }
        return defaultValue;
    }

    public static JsonNode getJson(ObjectNode o, String field) throws UtilsException {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            return ret;
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, field + " not found", null);
    }

    public static ObjectNode getObject(ObjectNode o, String field) throws ParseException, UtilsException {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            if (ret.isObject()) {
                return (ObjectNode) ret;
            }
            throw new ParseException(field + " is not an object node");
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, field + " not found", null);
    }

    public static int getInteger(ObjectNode o, String field) throws ParseException, UtilsException {
        if (o.has(field)) {
            JsonNode ret = o.get(field);
            if (ret.isInt()) {
                return ret.asInt();
            }
            throw new ParseException(field + " is not an integer");
        }
        throw new UtilsException(UtilsException.Type.PARAMETERS, field + " not found", null);
    }

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
