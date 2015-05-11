package org.repetti.utils.loaders;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.repetti.utils.ConfigLoader;
import org.repetti.utils.JsonHelper;
import org.repetti.utils.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Set;

/**
 * @author repetti
 */
public class MultivaluedConfigLoader extends ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(MultivaluedConfigLoader.class);
    private final MultivaluedMap<String, String> multivaluedMap;

    public MultivaluedConfigLoader(MultivaluedMap<String, String> multivaluedMap) {
        this.multivaluedMap = multivaluedMap;
    }

    /**
     * Checks if config has the key
     *
     * @return true if key is present (even if it is equal to null)
     */
    @Override
    public boolean has(String key) {
        return multivaluedMap.containsKey(key);
    }

    /**
     * Checks if value of the key is null
     *
     * @param throwException when true an exception will be thrown if the key is not present
     * @return true if throwException is false and key is not present or if the value of the key is null
     * @throws UtilsException if throwException is true and the key is not found
     */
    @Override
    public boolean isNull(String key, boolean throwException) throws UtilsException {
        List<String> ret = getInternal(key, throwException);
        return ret.size() <= 0;
    }

    @NotNull
    private List<String> getInternal(String key, boolean throwException) throws UtilsException {
        List<String> ret = multivaluedMap.get(key);
        if (ret == null) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        return ret;
    }

    /**
     * Checks if value of the key is String object (and not null)
     *
     * @param throwException when true an exception will be thrown if the key is not present
     * @return true if key is present, not null and instance of String
     * @throws UtilsException if throwException is true and the key is not found
     */
    @Override
    public boolean isString(String key, boolean throwException) throws UtilsException {
        List<String> ret = getInternal(key, throwException);
        return ret.size() == 1;
    }

    @Override
    public List<String> getStrings(String key) throws UtilsException {
        return getInternal(key, true);
    }

    /**
     * Gets the value of the key as a boolean value
     *
     * @param key Name of the key
     * @return true if key is present and equals true, otherwise false
     */
    @Override
    public boolean getBoolean(String key) throws UtilsException {
        List<String> ret = multivaluedMap.get(key);
        if (ret == null) {
            throw getException(ExceptionType.NOT_FOUND, key);
        }
        return ret.size() == 1 && Boolean.parseBoolean(ret.get(0));
    }

    @Override
    public boolean getBooleanSafe(String key, boolean defaultValue) {
        List<String> ret = multivaluedMap.get(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret.size() == 1 && Boolean.parseBoolean(ret.get(0));
    }

    @Override
    public ConfigLoader getConfig(String key) throws UtilsException {
        return null;
    }

    /**
     * Returns list of configs from an array
     *
     * @param key            name of the node containing an array
     * @param throwException if true and key doesn't exist or value is not an array of ObjectNodes an exception will
     *                       be thrown. Otherwise null will be returned on error.
     * @return null on error
     * @throws UtilsException if throwException and error occurs
     */
    @Override
    public List<ConfigLoader> getConfigs(String key, boolean throwException) throws UtilsException {
        return null;
    }

    @Override
    public int getInteger(String key) throws UtilsException {
        log.trace("getInteger({})", key);
        List<String> ret = getInternal(key, true);
        if (ret.size() == 1) {
            try {
                return Integer.parseInt(ret.get(0));
            } catch (NumberFormatException e) {
                throw getException(ExceptionType.PARSING, key);
            }
        }
        throw getException(ExceptionType.CAST, key);
    }

    @Override
    public int getIntegerSafe(String key, int defaultValue) {
        log.trace("getIntegerSafe({},{})", key, defaultValue);
        List<String> ret = getInternalSafe(key);
        if (ret == null || ret.size() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(ret.get(0));
        } catch (NumberFormatException e) {
            log.trace("unable to parse as int: {}", ret, e);
            return defaultValue;
        }
    }

    @Nullable
    private List<String> getInternalSafe(String key) {
        List<String> ret = multivaluedMap.get(key);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    @Override
    public JsonNode getJson(String key, boolean throwException) throws UtilsException {
        return JsonHelper.parse(getString(key, throwException));
    }

    @Override
    public String getString(String key) throws UtilsException {
        List<String> ret = getInternal(key, true);
        if (ret.size() == 1) {
            return ret.get(0);
        }
        throw getException(ExceptionType.CAST, key);
    }

    @Override
    public String getStringSafe(String key) {
        List<String> ret = getInternalSafe(key);
        if (ret != null && ret.size() == 1) {
            return ret.get(0);
        }
        return null;
    }

    /**
     * Gets names of the fields
     */
    @Override
    public Set<String> getFieldNames() {
        return multivaluedMap.keySet();
    }
}
