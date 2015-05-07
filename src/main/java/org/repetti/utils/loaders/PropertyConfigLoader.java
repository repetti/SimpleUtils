package org.repetti.utils.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.repetti.utils.ConfigLoader;
import org.repetti.utils.JsonHelper;
import org.repetti.utils.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author repetti
 */
public class PropertyConfigLoader extends ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(PropertyConfigLoader.class);
    private final Properties prop;

    public PropertyConfigLoader(Properties prop) throws UtilsException {
        log.trace("PropertyConfigLoader({})", prop);
        if (prop == null) {
            throw getException(ExceptionType.IO, "[PropertyConfigLoader] null");
        }
        this.prop = prop;
    }

    @Override
    public boolean isNull(String key, boolean throwException) throws UtilsException {
        if (!has(key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            return true;
        }
        Object o = prop.get(key);
        return o == null || "".equals(o);
    }

    @Override
    public boolean has(String key) {
        log.trace("has({})", key);
        return prop.containsKey(key);
    }

    @Override
    public boolean isString(String key, boolean throwException) throws UtilsException {
        if (!has(key)) {
            if (throwException) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            return false;
        }
        Object o = prop.get(key);
        return o != null && o instanceof String && !("".equals(o));
    }

    @Override
    public List<String> getStrings(String key) throws UtilsException {
        log.trace("getStrings({}, x={})", key);
        String val = prop.getProperty(key);
        if (val == null) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        //todo implement
        return Arrays.asList(val);
    }

    @Override
    public int getInteger(String key) throws UtilsException {
        log.trace("getInteger({})", key);
        String s = prop.getProperty(key);
        if (s == null) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        try {
            return Integer.parseInt(prop.getProperty(key));
        } catch (NumberFormatException e) {
            throw getException(ExceptionType.PARSING, key);
        }
    }

    @Override
    public int getIntegerSafe(String key, int defaultValue) {
        log.trace("getIntegerSafe({},{})", key, defaultValue);
        String s = prop.getProperty(key);
        if (s == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public JsonNode getJson(String key, boolean throwException) throws UtilsException {
        return JsonHelper.parse(getString(key, throwException));
    }

    @Override
    public String getString(String key) throws UtilsException {
        log.trace("getString({})", key);
        String val = prop.getProperty(key);
        if (val == null) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        return val;
    }

    @Override
    public String getStringSafe(String key) {
        log.trace("getStringSafe({})", key);
        return prop.getProperty(key);
    }

    @Override
    public Set<String> getFieldNames() {
        return prop.stringPropertyNames();
    }

    @Override
    public ConfigLoader getConfig(String key) throws UtilsException {
        log.trace("getConfig({})", key);
        try {
            return new JsonConfigLoader((ObjectNode) JsonHelper.mapper.readTree(getString(key, true)));
        } catch (IOException e) {
            throw getException(ExceptionType.PARSING, key);

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
        JsonNode obj;
        try {
            obj = JsonHelper.mapper.readTree(getString(key, true));
        } catch (IOException e) {
            throw getException(ExceptionType.PARSING, key);
        }
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
        log.trace("getBooleanSafe({})", key);
        return getBooleanSafe(prop, key, defaultValue);
    }

    public static boolean getBooleanSafe(Properties prop, String key, boolean defaultValue) {
        log.trace("getBooleanSafe({},{})", prop, key);
        String s = prop.getProperty(key);
        if (s == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(s);
    }

    @Override
    public boolean getBoolean(String key) throws UtilsException {
        log.trace("getBoolean({})", key);
        return getBoolean(prop, key);
    }

    public static boolean getBoolean(Properties prop, String key) throws UtilsException {
        log.trace("getBoolean({},{})", prop, key);
        String s = prop.getProperty(key);
        if (s == null) {
            throw getException(ExceptionType.NOT_FOUND, key + " is null");
        }
        return Boolean.parseBoolean(s);
    }

}
