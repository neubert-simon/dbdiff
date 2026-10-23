package db.connection.dataSources;

import db.connection.SupportedDbEngine;
import db.connection.configs.ConnectionConfiguration;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;

@FunctionalInterface
public interface DataSourceFactory {

    String JDBC_PREFIX = "jdbc:";

    /**
     * Contract for factories producing {@link DataSource} objects representing a database to interface with.
     * Classes implementing this interface are used in the {@link db.connection.DataSourceDispatcher}.
     *
     * @param connectionConfiguration Connection config holding the information needed to connect to the database.
     * @return Returns the DataSource object representing the database, including the actual connection.
     * @throws SQLException if the DataSource can't be created
     */
    DataSource createDataSource(@NonNull ConnectionConfiguration connectionConfiguration) throws SQLException;

    /**
     * Checks if the provided database connection string is formatted with the prefix {@code "jdbc:"}
     * and optionally the second prefix following it. If not, it creates a String of that format from the provided url.
     * This method only checks if the prefixes are present and adds them uncritically if not, it does not check if
     * the rest of the url is a valid connection string.
     *
     * @param url    The url to parse.
     * @param engine The engine whose connection string prefix should follow the JDBC prefix, can be null.
     * @return The validated connection string.
     */
    default String createValidJdbcUrl(@NonNull String url, @Nullable SupportedDbEngine engine) {

        String prefix = engine == null ? "" : engine.getConnectionStringPrefix();

        if (!url.startsWith(JDBC_PREFIX + prefix) && !url.startsWith(prefix)) {
            return JDBC_PREFIX + prefix + url;
        }

        if (url.startsWith(prefix)) {
            url = JDBC_PREFIX + url;
        }

        return url.startsWith(JDBC_PREFIX) ? url : JDBC_PREFIX + url;
    }
}
