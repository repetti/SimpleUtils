package org.repetti.utils.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.repetti.utils.ConfigLoader;
import org.repetti.utils.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author repetti
 */
public class JsonConfigLoader extends ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(JsonConfigLoader.class);
    private final ObjectNode node;

    public JsonConfigLoader(ObjectNode node) throws UtilsException {
        log.trace("JsonConfigLoader({})", node);
        if (node == null) {
            throw getException(ExceptionType.NOT_FOUND, "[JsonConfigLoader] null");
        }
        this.node = node;
    }

    @NotNull
    public static String getStringNotNull(ObjectNode o, String key) throws UtilsException {
        log.trace("getStringNotNull({},{})", o, key);
        String ret = getString(o, key);
        if (ret == null) {
            throw getException(ExceptionType.PARSING, "'" + key + "' should not be null");
        }
        return ret;
    }

    public static String getString(ObjectNode node, String key) throws UtilsException {
        log.trace("getString({},{})", key);
        if (!node.has(key)) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        final JsonNode j = node.get(key);
        return j.isNull() ? null : j.asText();
    }

    public static ObjectNode getConfig(ObjectNode node, String key) throws UtilsException {
        log.trace("getConfig({},{})", node, key);
        if (!has(node, key)) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        try {
            return (ObjectNode) node.get(key);
        } catch (ClassCastException e) {
            throw getException(ExceptionType.CAST, key);
        }
    }

    public static boolean has(ObjectNode node, String key) {
        log.trace("has({})", key);
        return node.has(key);
    }

    public static List<JsonNode> getArray(ObjectNode node, String key, boolean throwException) throws UtilsException {
        log.trace("getArray({},{},{})", node, key, throwException);
        if (!has(node, key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            } else {
                return null;
            }
        }
        JsonNode obj = node.get(key);
        if (!obj.isArray()) {
            if (throwException) {
                throw getException(ExceptionType.CAST, key + obj);
            } else {
                return null;
            }
        }
        List<JsonNode> ret = new ArrayList<JsonNode>();
        for (JsonNode tmp : (ArrayNode) obj) {
            ret.add(tmp);
        }
        return ret;
    }

    public static List<ObjectNode> getConfigs(ObjectNode node, String key, boolean throwException) throws UtilsException {
        log.trace("getConfigs({},{},{})", node, key, throwException);
        if (!has(node, key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            } else {
                return null;
            }
        }
        JsonNode obj = node.get(key);
        if (!obj.isArray()) {
            if (throwException) {
                throw getException(ExceptionType.CAST, key + obj);
            } else {
                return null;
            }
        }
        List<ObjectNode> ret = new ArrayList<ObjectNode>();
        for (JsonNode tmp : (ArrayNode) obj) {
            if (tmp.isObject()) {
                ret.add((ObjectNode) tmp);
            } else {
                if (throwException) {
                    throw getException(ExceptionType.CAST, tmp);
                } else {
                    return null;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean isNull(String key, boolean throwException) throws UtilsException {
        return isNull(node, key, throwException);
    }

    public static boolean isNull(ObjectNode node, String key, boolean throwException) throws UtilsException {
        if (!has(node, key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            return true;
        }
        return node.get(key).isNull();
    }

    @Override
    public boolean isString(String key, boolean throwException) throws UtilsException {
        if (!has(key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            return false;
        }
        return node.get(key).isTextual();
    }

    @Override
    public boolean has(String key) {
        log.trace("has({})", key);
        return node.has(key);
    }

    @Override
    public String getString(String key) throws UtilsException {
        log.trace("getString({})", key);
        return getString(node, key);
    }

    @Override
    public String getStringSafe(String key) {
        log.trace("getStringSafe({})", key);
        return getStringSafe(node, key);
    }

    public static String getStringSafe(ObjectNode node, String key) {
        log.trace("getStringSafe({},{})", node, key);
        if (!node.has(key)) {
            return null;
        }
        final JsonNode j = node.get(key);
        return j.isNull() ? null : j.asText();
    }

    @Override
    public List<String> getStrings(String key) throws UtilsException {
        log.trace("getStrings({})", key);
        return getStrings(node, key);
    }

    public static List<String> getStrings(ObjectNode node, String key) throws UtilsException {
        log.trace("getStrings({},{})", node, key);
        if (!node.has(key)) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        List<String> ret = new ArrayList<String>();
        for (JsonNode j : node.get(key)) {
            ret.add(j.asText());
        }
        return ret;
    }

    @Override
    public int getInteger(String key) throws UtilsException {
        log.trace("getInteger({})", key);
        JsonNode j = node.get(key);
        if (j == null || j.isNull() || !j.isNumber()) {
            throw getException(ExceptionType.NOT_FOUND, key + " not found");
        }
        return j.asInt();
    }

    @Override
    public int getIntegerSafe(String key, int defaultValue) {
        log.trace("getIntegerSafe({},{})", key, defaultValue);
        JsonNode j = node.get(key);
        if (j == null || j.isNull() || !j.isNumber()) {
            return defaultValue;
        }
        return j.asInt();
    }

    @Override
    public JsonNode getJson(String key, boolean throwException) throws UtilsException {
        if (node.has(key)) {
            return node.get(key);
        }
        if (throwException) {
            throw getException(ExceptionType.NOT_FOUND, key);
        } else {
            return NullNode.getInstance();
        }
    }

    @Override
    public Set<String> getFieldNames() {
        Set<String> ret = new HashSet<String>();
        Iterator<String> i = node.fieldNames();
        while (i.hasNext()) {
            ret.add(i.next());
        }
        return ret;
    }

    @Override
    public ConfigLoader getConfig(String key) throws UtilsException {
        log.trace("getConfig({})", key);
        if (!has(key)) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        try {
            return new JsonConfigLoader((ObjectNode) node.get(key));
        } catch (ClassCastException e) {
            throw getException(ExceptionType.CAST, key);
        }
    }

    @Override
    public List<ConfigLoader> getConfigs(String key, boolean throwException) throws UtilsException {
        log.trace("getConfigs({},{})", key, throwException);
        if (!has(key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            } else {
                return null;
            }
        }
        JsonNode obj = this.node.get(key);
        if (!obj.isArray()) {
            if (throwException) {
                throw getException(ExceptionType.CAST, key + obj);
            } else {
                return null;
            }
        }
        List<ConfigLoader> ret = new ArrayList<ConfigLoader>();
        for (JsonNode tmp : (ArrayNode) obj) {
            if (tmp.isObject()) {
                ret.add(new JsonConfigLoader((ObjectNode) tmp));
            } else {
                if (throwException) {
                    throw getException(ExceptionType.CAST, tmp);
                } else {
                    return null;
                }
            }
        }
        return ret;
    }

    @Override
    public boolean getBooleanSafe(String key, boolean defaultValue) {
        log.trace("getBoolean({})", key);
        return getBooleanSafe(node, key, defaultValue);
    }

    public static boolean getBooleanSafe(ObjectNode node, String key, boolean defaultValue) {
        log.trace("getBoolean({},{})", node, key);
        return node.has(key) && !node.get(key).isNull() && node.get(key).asBoolean();
    }

    @Override
    public boolean getBoolean(String key) throws UtilsException {
        log.trace("getBoolean({})", key);
        return getBoolean(node, key);
    }

    public static boolean getBoolean(ObjectNode node, String key) throws UtilsException {
        log.trace("getBoolean({},{})", node, key);
        if (node.has(key)) {
            final JsonNode j = node.get(key);
            if (j.isBoolean()) {
                return j.asBoolean();
            } else {
                throw getException(ExceptionType.CAST, key + " is not boolean: " + j);
            }
        }
        throw getException(ExceptionType.NOT_FOUND, key);
    }

    @Override
    public String toString() {
        return "JsonConfigLoader{" + node + '}';
    }
}
