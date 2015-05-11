package org.repetti.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author repetti
 */
public class MongoExecutor {
    private static final Logger log = LoggerFactory.getLogger(MongoExecutor.class);
    private static final WriteConcern defaultConcern = WriteConcern.SAFE; //WriteConcern.ACKNOWLEDGED;

    public static void put(@NotNull DBCollection dbCollection, @NotNull String id, String field, String value) throws DatabaseException {
        log.trace("put({}, {}:{})", id, field, value);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            query.put(field, value);
            put(dbCollection, query);
        } catch (MongoException e) {
            throw processException(e, "put");
        }
    }

    public static DBObject put(@NotNull DBCollection dbCollection, @NotNull DBObject object) throws DatabaseException {
        log.trace("put({})", object);
        try {
            if (!object.containsField(UtilsConstants.MONGO_PRIMARY_KEY)) {
                ObjectId id = new ObjectId();
                log.trace("put: id will be added = {}", id);
                object.put(UtilsConstants.MONGO_PRIMARY_KEY, id.toString());
            }
            dbCollection.insert(object, defaultConcern);
            return object;
        } catch (MongoException e) {
            throw processException(e, "put");
        }
    }

    private static DatabaseException processException(@NotNull Exception e, String method) throws DatabaseException {
        throw new DatabaseException(DatabaseException.Type.RUNTIME, "unable to process " + method, e);
    }

    public static String put(@NotNull DBCollection dbCollection, String field, String value) throws DatabaseException {
        log.trace("put({}:{})", field, value);
        try {
            BasicDBObject query = new BasicDBObject();
//            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            query.put(field, value);
            put(dbCollection, query);
            return query.getString(UtilsConstants.MONGO_PRIMARY_KEY);
        } catch (MongoException e) {
            throw processException(e, "put");
        }
    }

    public static void update(@NotNull DBCollection dbCollection, @NotNull String id, @NotNull String field, Object value) throws DatabaseException {
        log.trace("put({}, {}:{})", id, field, value);
        update(dbCollection, id, new BasicDBObject(field, value));
    }

    /**
     * Updates multiple fields
     *
     * @param dbCollection collection to be updated
     * @param id           object id to be changed
     * @param record       map (filed -> newValue)
     * @throws DatabaseException if error occurs
     */
    public static void update(@NotNull DBCollection dbCollection, @NotNull String id, @NotNull BasicDBObject record) throws DatabaseException {
        log.trace("put({},{})", id, record);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            dbCollection.update(query, new BasicDBObject("$set", record), true, false, defaultConcern);
        } catch (MongoException e) {
            throw processException(e, "update");
        }
    }

    public static DBObject findAndUpdate(DBCollection dbCollection, String id, String updateField, Object value) {
        BasicDBObject query = new BasicDBObject();
        query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
        return dbCollection.findAndModify(query, new BasicDBObject("$set", new BasicDBObject(updateField, value)));
    }

    public static boolean contains(@NotNull DBCollection dbCollection, @NotNull String id) throws DatabaseException {
        log.trace("contains({})", id);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            return dbCollection.count(query) == 1;
        } catch (MongoException e) {
            throw processException(e, "contains");
        }
    }

    public static boolean contains(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query) throws DatabaseException {
        log.trace("contains({})", query);
        try {
            return dbCollection.count(query) >= 1;
        } catch (MongoException e) {
            throw processException(e, "contains");
        }
    }

//    /**
//     * Returns object, converted to String
//     */
//    public static String getFirstString(@NotNull DBCollection dbCollection, @NotNull DBObject query, String field) throws DatabaseException {
//        log.trace("get({},{})", query, field);
//        final Object obj = find(dbCollection, query, field);
//        return serialize(obj);
//    }

    /**
     * Returns object, casted to String
     */
    public static String get(@NotNull DBCollection dbCollection, @NotNull String id, String field) throws DatabaseException {
        log.trace("get({},{})", id, field);
        return (String) getObject(dbCollection, id, field);
    }

    /**
     * Gets a value of the field of the object with ID specified. If document with this ID not found an exception is
     * thrown. That also will be done on connection problem.
     *
     * @param dbCollection collection to work with
     * @param id           ID of the object
     * @param field        value of the field to return
     * @return value of the field specified
     * @throws DatabaseException NOT_FOUND if record not found, RUNTIME on connection problems
     */
    @Nullable
    public static Object getObject(@NotNull DBCollection dbCollection, @NotNull String id, String field) throws DatabaseException {
        log.trace("getObject({},{})", id, field);
        return getRecord(dbCollection, id, field).get(field);
    }

    @NotNull
    public static DBObject getRecord(@NotNull DBCollection dbCollection, @NotNull String id, @NotNull String... fields) throws DatabaseException {
        if (log.isTraceEnabled()) {
            log.trace("getRecord({},{})", id, Arrays.toString(fields));
        }
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            BasicDBObject fieldsRec = new BasicDBObject();
            int cnt = 1;
            for (String field : fields) {
                fieldsRec.put(field, cnt++);
            }
            DBObject record = dbCollection.findOne(query, fieldsRec);
            if (record == null) {
                throw new DatabaseException(DatabaseException.Type.NOT_FOUND, "document not found", null);
            }
            return record;
        } catch (MongoException e) {
            throw processException(e, "getRecord");
        }
    }

    /**
     * Returns object, converted to String
     */
    public static String getString(@NotNull DBCollection dbCollection, @NotNull String id, String field) throws DatabaseException {
        log.trace("get({},{})", id, field);
        final Object obj = getObject(dbCollection, id, field);
        return serialize(obj);
    }

    public static String serialize(Object obj) {
        return obj == null ? null : JSON.serialize(obj);
    }

    public static boolean getBoolean(@NotNull DBCollection dbCollection, @NotNull String id, String field) throws DatabaseException {
        log.trace("getBoolean({},{})", id, field);
        return getObject(dbCollection, id, field) == Boolean.TRUE;
    }

    public static DBCursor findCursor(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query) throws DatabaseException {
        return dbCollection.find(query, new BasicDBObject(UtilsConstants.MONGO_PRIMARY_KEY, 1));
    }

    public static DBCursor findCursor(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query, @NotNull String... fields) throws DatabaseException {
        BasicDBObject f = new BasicDBObject();
        int i = 1;
        for (String s : fields) {
            f.put(s, i++);
        }
        return dbCollection.find(query, f);
    }

    /**
     * @return ids of documents
     */
    public static List<String> findIds(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query) throws DatabaseException {
        return findField(dbCollection, query, UtilsConstants.MONGO_PRIMARY_KEY);
    }

    public static List<String> findField(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query, @NotNull String field) throws DatabaseException {
        log.trace("find({})", query);
        try {
            DBCursor record = findCursor(dbCollection, query, field); // 1?
            List<String> ret = new ArrayList<String>();
            for (DBObject obj : record) {
                ret.add((String) obj.get(field)); //todo check
            }
            return ret;
        } catch (MongoException e) {
            throw processException(e, "find");
        }
    }

    public static DBCursor findCursor(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query, String field) throws DatabaseException {
        return dbCollection.find(query, new BasicDBObject(field, 1));
    }

    public static Object findOne(@NotNull DBCollection dbCollection, @NotNull BasicDBObject query, String field) throws DatabaseException {
        log.trace("findOne({})", query);
        try {
            DBCursor record = findCursor(dbCollection, query, field);
            if (record.hasNext()) {
                DBObject obj = record.next();
                //check more than one
                return obj.get(field);
            }
            return null;
        } catch (MongoException e) {
            throw processException(e, "findOne");
        }
    }

    public static void remove(@NotNull DBCollection dbCollection, @NotNull String id) throws DatabaseException {
        log.trace("remove({})", id);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            WriteResult res = dbCollection.remove(query, defaultConcern);
        } catch (MongoException e) {
            throw processException(e, "remove");
        }
    }

    /**
     * Used to delete documents by mongo-generated id's
     *
     * @param id string representation of the ObjectId
     * @deprecated
     */
    public static void removeId(@NotNull DBCollection dbCollection, @NotNull String id) throws DatabaseException {
        log.trace("removeId({})", id);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, new ObjectId(id));
            WriteResult res = dbCollection.remove(query, defaultConcern);
        } catch (MongoException e) {
            throw processException(e, "removeId");
        }
    }

    /**
     * On error an object may be lost.
     *
     * @param from source collection
     * @param to   destination collection
     * @param id   id of the object to move
     */
    public static void move(@NotNull DBCollection from, @NotNull DBCollection to, @NotNull String id) throws DatabaseException {
        log.trace("move({})", id);
        try {
            BasicDBObject query = new BasicDBObject();
            query.put(UtilsConstants.MONGO_PRIMARY_KEY, id);
            WriteResult res = to.insert(from.findAndRemove(query), defaultConcern);
        } catch (MongoException e) {
            throw processException(e, "move");
        }
    }

    public static void removeAll(@NotNull DBCollection dbCollection, @NotNull String field, String value) throws DatabaseException {
        log.trace("removeAll({},{})", field, value);
        BasicDBObject query = new BasicDBObject();
        query.put(field, value);
        removeAll(dbCollection, query);
    }

    public static void removeAll(@NotNull final DBCollection dbCollection, @NotNull final BasicDBObject query) throws DatabaseException {
        log.trace("removeAll()");
        try {
            WriteResult res = dbCollection.remove(query, defaultConcern);
        } catch (MongoException e) {
            throw processException(e, "removeAll");
//            throw new DatabaseException(DatabaseException.Type.RUNTIME, "removeAll: " + msg, null);
        }
    }
}