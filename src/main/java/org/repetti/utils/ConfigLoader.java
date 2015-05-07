package org.repetti.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.repetti.utils.loaders.JsonConfigLoader;
import org.repetti.utils.loaders.PropertyConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author repetti
 */
public abstract class ConfigLoader {

    private final static Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    public static ConfigLoader loadFile(InputStream inputStream, boolean jsonFormat) throws UtilsException {
        if (inputStream == null) {
            throw getException(ExceptionType.NOT_FOUND, "[loadFile] null");
        }
        return jsonFormat ?
                loadJsonStream(inputStream) :
                loadPropertiesStream(inputStream);
    }

    protected static UtilsException getException(ExceptionType type, Object comment) {
        return getException(type, comment, null);
    }

    private static ConfigLoader loadJsonStream(final InputStream inputStream) throws UtilsException {
        JsonNode node = null;
        try {
            return new JsonConfigLoader((ObjectNode) (node = JsonHelper.mapper.readTree(inputStream)));
        } catch (ClassCastException e) {
            throw getException(ExceptionType.CAST, node, e);
        } catch (IOException e) {
            throw getException(ExceptionType.PARSING, inputStream, e);
        }
    }

    private static ConfigLoader loadPropertiesStream(final InputStream inputStream) throws UtilsException {
        Properties prop = new Properties();
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            throw getException(ExceptionType.IO, inputStream);
        }
        return new PropertyConfigLoader(prop);
    }

    protected static UtilsException getException(ExceptionType type, Object comment, Exception cause) {
        final String text = new StringBuilder("[").append(type.toString()).append("] ").append(comment).toString();
        log.warn(text);
        return new UtilsException(UtilsException.Type.PARAMETERS, text, cause);
    }

    public static ConfigLoader loadMap(Map map) throws UtilsException {
        return new MapConfigLoader(map);
    }

    /**
     * Should be used to parse main method parameters.
     * <p/>
     * Now supports only one-letter-keys starting with symbol defined
     *
     * @param paramSymbol first symbol of key
     * @param args        list of arguments
     * @return MapConfigLoader with parameters
     */
    public static ConfigLoader loadArgs(@NotNull final String paramSymbol, String... args) throws UtilsException {
        if (paramSymbol == null || paramSymbol.length() == 0) {
            throw new UtilsException(UtilsException.Type.PARAMETERS, "paramSymbol should be at least one symbol", null);
        }
        String lastParam = null;
        final int introLength = paramSymbol.length();
        Map<String, String> map = new HashMap<String, String>();
        for (String s : args) {
            if (s.startsWith(paramSymbol)) {
                if (lastParam != null) {
                    map.put(lastParam, null);
                    lastParam = null;
                }
                lastParam = s.substring(introLength);
            } else {
                if (lastParam != null) {
                    map.put(lastParam, s);
                    lastParam = null;
                }
            }
        }
        if (lastParam != null) {
            map.put(lastParam, null);
        }
        return new MapConfigLoader(map);
    }

    public static List<ConfigLoader> loadJsonArray(final ArrayNode node) throws UtilsException {
        List<ConfigLoader> ret = new ArrayList<ConfigLoader>(node.size());
        for (JsonNode json : node) {
            try {
                ret.add(loadJson((ObjectNode) json));
            } catch (ClassCastException e) {
                throw getException(ExceptionType.CAST, json);
            }
        }
        return ret;
    }

    public static ConfigLoader loadJson(final ObjectNode node) throws UtilsException {
        return new JsonConfigLoader(node);
    }

    public static ConfigLoader loadJsonBytes(final byte[] json) throws UtilsException {
        final JsonNode jsonNode;
        try {
            jsonNode = JsonHelper.mapper.readTree(json);
        } catch (IOException e) {
            throw getException(ExceptionType.PARSING, new String(json));
        }
        final ObjectNode node;
        try {
            node = (ObjectNode) jsonNode;
        } catch (ClassCastException e) {
            throw getException(ExceptionType.CAST, jsonNode);
        }
        return new JsonConfigLoader(node);
    }

    public static ConfigLoader loadJsonString(final String json) throws UtilsException {
        final JsonNode jsonNode;
        try {
            jsonNode = JsonHelper.mapper.readTree(json);
        } catch (IOException e) {
            throw getException(ExceptionType.PARSING, json);
        }
        final ObjectNode node;
        try {
            node = (ObjectNode) jsonNode;
        } catch (ClassCastException e) {
            throw getException(ExceptionType.CAST, jsonNode);
        }
        return new JsonConfigLoader(node);
    }

    /**
     * Checks if config has the key
     *
     * @return true if key is present (even if it is equal to null)
     */
    public abstract boolean has(String key);

    /**
     * Checks if value of the key is null
     *
     * @param throwException when true an exception will be thrown if the key is not present
     * @return true if throwException is false and key is not present or if the value of the key is null
     * @throws UtilsException if throwException is true and the key is not found
     */
    public abstract boolean isNull(String key, boolean throwException) throws UtilsException;

    /**
     * Checks if value of the key is String object (and not null)
     *
     * @param throwException when true an exception will be thrown if the key is not present
     * @return true if key is present, not null and instance of String
     * @throws UtilsException if throwException is true and the key is not found
     */
    public abstract boolean isString(String key, boolean throwException) throws UtilsException;

    /**
     * Returns list of configs from an array
     *
     * @param key            name of the node containing an array
     * @param throwException if true and key doesn't exist or value is not an array of ObjectNodes an exception will
     *                       be thrown. Otherwise null will be returned on error.
     * @return null on error
     * @throws UtilsException if throwException and error occurs
     */
    public abstract List<ConfigLoader> getConfigs(String key, boolean throwException) throws UtilsException;

    public abstract List<String> getStrings(String key) throws UtilsException;

    /**
     * Gets value as int value
     *
     * @param key field name
     * @return value
     * @throws UtilsException if the field not found or bad format
     */
    public abstract int getInteger(String key) throws UtilsException;

    /**
     * Same as getInteger but returns 0 if not present or bad format
     */
    public abstract int getIntegerSafe(String key, int defaultValue);

    public abstract JsonNode getJson(String key, boolean throwException) throws UtilsException;

    public File getFile(String key) throws UtilsException {
        return getConfig(key).getFile();
    }

//    protected UtilsException getExceptionKeyNotFound(String key) {
//        return new UtilsException(UtilsException.Type.NOT_FOUND, "key '" + key + "' not found", null);
//    }

    /**
     * Parses config parameter, defined as key. And returns file object if everything was correct
     *
     * @return File instance
     */
    public File getFile() throws UtilsException {
        final boolean absolute = getBoolean(UtilsConstants.FILE_FULL);
        final String filename = getString(UtilsConstants.FILE_NAME, true);
        return ResourceHelper.getFile(filename, !absolute);
    }

    public abstract ConfigLoader getConfig(String key) throws UtilsException;

    /**
     * Gets the value of the key as a boolean value
     *
     * @param key field
     * @return boolean value
     * @throws UtilsException if not found or other format
     */
    public abstract boolean getBoolean(String key) throws UtilsException;

    /**
     * @param key            node field
     * @param throwException on true an exception will be thrown
     * @return the value of the field or null if it is not found
     * @throws UtilsException if the field not found and throwException is true
     */
    @Nullable
    public String getString(String key, boolean throwException) throws UtilsException {
        if (throwException) {
            return getString(key);
        } else {
            return getStringSafe(key);
        }
    }

    @Nullable
    public abstract String getString(String key) throws UtilsException;

    @Nullable
    public abstract String getStringSafe(String key);

    /**
     * Gets the value of the key as a boolean value
     *
     * @param key Name of the key
     * @return true if key is present and equals true, otherwise false
     */
    public abstract boolean getBooleanSafe(String key, boolean defaultValue);

    @NotNull
    public String getStringNotNull(String key) throws UtilsException {
        String ret = getString(key);
        if (ret == null) {
            throw getException(ExceptionType.PARSING, "'" + key + "' should not be null");
        }
        return ret;
    }

    public InputStream getInputStream(String key) throws UtilsException {
        return getConfig(key).getInputStream();
    }

    /**
     * Parses config parameter, defined as key. And returns file object if everything was correct
     *
     * @return File instance
     */
    public InputStream getInputStream() throws UtilsException {
        final boolean absolute = getBoolean(UtilsConstants.FILE_FULL);
        final String filename = getString(UtilsConstants.FILE_NAME, true);
        return ResourceHelper.getStream(filename, !absolute);
    }

    /**
     * Gets names of the fields
     */
    public abstract Set<String> getFieldNames();

    protected static enum ExceptionType {
        NOT_FOUND, CAST, IO, PARSING
    }

    private static class MapConfigLoader extends ConfigLoader {
        //        private static final Logger log = LoggerFactory.getLogger(MapConfigLoader.class);
        private final Map<String, Object> map;

        public MapConfigLoader(Map map) throws UtilsException {
            if (map == null) {
                throw getException(ExceptionType.NOT_FOUND, "MapConfigLoader");
            }
            this.map = map;
        }

        @Override
        public boolean isNull(String key, boolean throwException) throws UtilsException {
            if (!has(key)) {
                if (throwException) {
                    throw getException(ExceptionType.NOT_FOUND, key);
                }
                return true;
            }
            return map.get(key) == null;
        }

        @Override
        public boolean has(String key) {
            log.trace("has({})", key);
            return map.containsKey(key);
        }

        @Override
        public boolean isString(String key, boolean throwException) throws UtilsException {
            if (!has(key)) {
                if (throwException) {
                    throw getException(ExceptionType.NOT_FOUND, key);
                }
                return false;
            }
            Object o = map.get(key);
            return o != null && o instanceof String;
        }

        @Override
        public String getString(String key) throws UtilsException {
            if (!map.containsKey(key)) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            final Object o = map.get(key);
            return o == null ? null : o.toString();
        }

        @Override
        public String getStringSafe(String key) {
            if (!map.containsKey(key)) {
                return null;
            }
            final Object o = map.get(key);
            return o == null ? null : o.toString();
        }

        @Override
        public List<String> getStrings(String key) throws UtilsException {
            if (!map.containsKey(key)) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            Object ret = map.get(key);
            if (ret instanceof List) {
                try {
                    return (List<String>) ret;
                } catch (ClassCastException e) {
                    throw getException(ExceptionType.CAST, key + " is not a list of Strings");
                }
            }
            throw getException(ExceptionType.CAST, key + " is not a list");
        }

        @Override
        public int getInteger(String key) throws UtilsException {
            log.trace("getInteger({})", key);
            Object o = map.get(key);
            if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            throw getException(ExceptionType.CAST, key + " is not numeric: " + o);
        }

        @Override
        public int getIntegerSafe(String key, int defaultValue) {
            log.trace("getIntegerSafe({})", key);
            Object o = map.get(key);
            if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            return defaultValue;
            //new UtilsException(UtilsException.Type.PARSING);
        }

        @Override
        public JsonNode getJson(String key, boolean throwException) throws UtilsException {
            throw new UtilsException(UtilsException.Type.UNDEFINED, "not implemented", null);
        }

        @Override
        public Set<String> getFieldNames() {
            return map.keySet();
        }

        @Override
        public ConfigLoader getConfig(String key) throws UtilsException {
            log.trace("getConfig({})", key);
            if (!has(key)) {
                throw getException(ExceptionType.NOT_FOUND, key);
            }
            try {
                return new MapConfigLoader((Map) this.map.get(key));
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
            Object obj = this.map.get(key);
            if (!(obj instanceof Collection)) {
                if (throwException) {
                    throw getException(ExceptionType.CAST, key + obj);
                } else {
                    return null;
                }
            }
            List<ConfigLoader> ret = new ArrayList<ConfigLoader>();
            for (Object tmp : (Collection) obj) {
                if (tmp instanceof Map) {
                    ret.add(new MapConfigLoader((Map) tmp));
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
            if (map.containsKey(key)) {
                Object value = map.get(key);
                return value != null && value instanceof Boolean && ((Boolean) map.get(key));
            }
            return defaultValue;
        }

        @Override
        public boolean getBoolean(String key) throws UtilsException {
            log.trace("getBoolean({})", key);
            if (map.containsKey(key)) {
                Object value = map.get(key);
                if (value == null) {
                    throw getException(ExceptionType.NOT_FOUND, key + " is null");
                }
                if (value instanceof Boolean) {
                    return (Boolean) value;
                }
                throw getException(ExceptionType.CAST, key + " is " + value);
            }
            throw getException(ExceptionType.NOT_FOUND, key);
        }
    }
}
