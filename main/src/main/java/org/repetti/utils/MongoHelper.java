package org.repetti.utils;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Workflow: connect -> getDatabase  -> work with DB object
 * <p/>
 *
 * @author repetti
 */
public class MongoHelper {
    private static final Logger log = LoggerFactory.getLogger(MongoHelper.class);

    public static MongoClient connect(@NotNull ConfigLoader configLoader) throws UtilsException {
        final boolean useSingleHost = configLoader.getBoolean(UtilsConstants.MONGO_USE_SINGLE_HOST);
        List<ConfigLoader> configs = configLoader.getConfigs(UtilsConstants.MONGO_HOSTS, true);
        if (configs.size() == 0) {
            throw new UtilsException(UtilsException.Type.PARAMETERS, UtilsConstants.MONGO_HOSTS + " is empty", null);
        }
        if (useSingleHost) {
            log.info("Mongo single host mode will be used.");
            ConfigLoader cl = configs.get(0);
            log.trace("mongo host to be used: {}", cl);
            return connect(createAddress(
                            cl.getString(UtilsConstants.MONGO_HOST),
                            cl.getInteger(UtilsConstants.MONGO_PORT)),
                    createCredentialsIfRequired(configLoader));
        } else {
            List<ServerAddress> replicaSetSeeds = new ArrayList<ServerAddress>();
            for (ConfigLoader cl : configs) {
                log.trace("mongo host to be added: {}", cl);
                replicaSetSeeds.add(createAddress(cl.getString(UtilsConstants.MONGO_HOST, true), cl.getInteger(UtilsConstants.MONGO_PORT)));
            }
            return connect(replicaSetSeeds, createCredentialsIfRequired(configLoader));
        }
    }

    public static MongoClient connect(ServerAddress addr, @Nullable List<MongoCredential> credentials) throws UtilsException {
        try {
            return new MongoClient(addr, credentials);
        } catch (MongoException e) {
            log.error("error on mongo init", e);
            throw new UtilsException(UtilsException.Type.CONNECTION, "unable to connect to mongo.db", e);
        }
    }

    public static ServerAddress createAddress(String host, int port) throws UtilsException {
        try {
            return new ServerAddress(host, port);
        } catch (UnknownHostException e) {
            log.error("error on mongo init, bad config or network configuration", e);
            throw new UtilsException(UtilsException.Type.PARAMETERS, "mongo.db host not found", e);
        }
    }

    @Nullable
    private static List<MongoCredential> createCredentialsIfRequired(ConfigLoader configLoader) throws UtilsException {
        if (isAuthorizationRequired(configLoader)) {
            return createCredentials(configLoader);
        } else {
            return null;
        }
    }

    public static MongoClient connect(@NotNull List<ServerAddress> replicaSetSeeds, @Nullable List<MongoCredential> credentials) throws UtilsException {
        log.debug("Connecting to mongo, replica seeds: {}", replicaSetSeeds);
        if (replicaSetSeeds.size() == 0) {
            throw new UtilsException(UtilsException.Type.PARAMETERS, "list of mongo hosts cannot be empty", null);
        }
        return new MongoClient(replicaSetSeeds, credentials);
    }

    private static boolean isAuthorizationRequired(@NotNull ConfigLoader configLoader) throws UtilsException {
        boolean ret = configLoader.getBoolean(UtilsConstants.MONGO_AUTHORIZE);
        log.info("Authorisation {}", ret ? "required" : "disabled");
        return ret;
    }

    /**
     * @param configLoader configuration data
     * @throws UtilsException if any parameter not found
     * @since 0.5
     */
    public static List<MongoCredential> createCredentials(ConfigLoader configLoader) throws UtilsException {
        return Arrays.asList(createCredential(configLoader));
    }

    /**
     * @param configLoader configuration data
     * @throws UtilsException if any parameter not found
     * @since 0.5
     */
    public static MongoCredential createCredential(ConfigLoader configLoader) throws UtilsException {
        //TODO support other auth types

        return createCRCredential(
                configLoader.getStringNotNull(UtilsConstants.MONGO_DATABASE_AUTH),
                configLoader.getStringNotNull(UtilsConstants.MONGO_USERNAME),
                configLoader.getStringNotNull(UtilsConstants.MONGO_PASSWORD)
        );
    }

    public static MongoCredential createCRCredential(@NotNull String authDB, @NotNull String username, @NotNull String password) {
        return MongoCredential.createMongoCRCredential(username, authDB, password.toCharArray());
    }

    public static MongoClient connect(ServerAddress addr) throws UtilsException {
        return connect(addr, null);
    }

    public static MongoClient connect(@NotNull List<ServerAddress> replicaSetSeeds) throws UtilsException {
        return connect(replicaSetSeeds, null);
    }

    public static DB getDataDatabase(@NotNull MongoClient mongo, @NotNull ConfigLoader configLoader) throws UtilsException {
        return mongo.getDB(configLoader.getString(UtilsConstants.MONGO_DATABASE_DATA, true));
    }

    public static DB getDatabase(@NotNull MongoClient mongo, @NotNull String dbName) {
        return mongo.getDB(dbName);
    }
}
