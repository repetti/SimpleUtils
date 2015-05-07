package org.repetti.utils;

/**
 * @author repetti
 */
public class DatabaseException extends Exception {
    public final Type type;

    public DatabaseException(Type type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
//    private final EnumSet<Type> recoverable = EnumSet.of(Type.PARAMETERS, Type.NOT_FOUND);

    public boolean isRecoverable() {
//        return recoverable.contains(type);
        return type.recoverable;
    }

    public enum Type {
        RUNTIME(false), CONNECTION(false),
        // recoverable
        PARAMETERS(true), NOT_FOUND(true);

        private final boolean recoverable;

        Type(boolean recoverable) {
            this.recoverable = recoverable;
        }
    }
}
