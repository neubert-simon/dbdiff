package db.connection;

public class UnsupportedDatabaseException extends RuntimeException {
    public UnsupportedDatabaseException(String message) {
        super(message);
    }
}
