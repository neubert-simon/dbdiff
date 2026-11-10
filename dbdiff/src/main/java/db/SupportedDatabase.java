package db;

import lombok.RequiredArgsConstructor;
import utility.ArgumentChecker;
import db.exceptions.UnsupportedDatabaseException;

import java.sql.DatabaseMetaData;

/**
 * Maps supported engine engines to their JDBC connection string prefix.
 */
@RequiredArgsConstructor
public enum SupportedDatabase {
    POSTGRESQL("postgresql"),
    MYSQL("mysql"),
    SQLITE("sqlite");;

    public final String connectionStringPrefix;

    public static SupportedDatabase getFromName(String name) throws UnsupportedDatabaseException {
        ArgumentChecker.requireNonBlank(name, "Database name");
        for (SupportedDatabase supportedDatabase : SupportedDatabase.values()) {
            if (supportedDatabase.name().equalsIgnoreCase(name)) {
                return supportedDatabase;
            }
        }
        throw new UnsupportedDatabaseException("Database " + name + " not supported");
    }

    /**
     * Get engine from corresponding databaseProductName returned by {@link DatabaseMetaData#getDatabaseProductName()}
     *
     * @param databaseProductName Product name of the engine.
     * @return The enum representing the engine.
     */
    public static SupportedDatabase getFromProductName(String databaseProductName) throws UnsupportedDatabaseException {
        ArgumentChecker.requireNonBlank(databaseProductName, "Database product name");
        for (SupportedDatabase supportedDatabase : SupportedDatabase.values()) {
            if (supportedDatabase.connectionStringPrefix.equalsIgnoreCase(databaseProductName)) {
                return supportedDatabase;
            }
        }
        throw new UnsupportedDatabaseException("Unsupported engine product: " + databaseProductName);
    }
}
