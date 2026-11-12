package db.connection.configuration.model;

import configuration.model.Configuration;

import java.nio.file.Path;

/**
 * Configuration to connect to a database with, including authentication and driver information.
 */
public interface ConnectionConfiguration extends Configuration {

    String JDBC_PREFIX = "jdbc:";

    /**
     * Fetches the {@link Path} to a user provided driver.
     * @return The {@link Path} to the driver.
     */
    Path getDriver();

    /**
     * Builds the JDBC connection string.<br>
     * Default implementation wraps {@link Object#toString()}.
     * @return The JDBC connection string as {@link String}
     */
    default String getJDBCConnectionString() {
        return this.toString();
    }

}
