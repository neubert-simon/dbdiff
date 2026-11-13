package db.connection;

import db.connection.configuration.model.ConnectionConfiguration;
import db.driver.DriverLoader;
import db.exceptions.DriverUnableToConnectException;
import db.exceptions.SQLExceptionHandler;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public class ConnectionDispatcherServiceLoader implements ConnectionDispatcher {

    private final ConnectionConfiguration configuration;
    private final Driver driver;

    /**
     * Constructs a {@link ConnectionDispatcherServiceLoader}.
     *
     * @param configuration {@link ConnectionConfiguration} to create connection with.
     * @param driver        {@link DriverLoader} to attempt connecting through.
     */
    public ConnectionDispatcherServiceLoader(@NonNull ConnectionConfiguration configuration, @NonNull Driver driver) {
        this.configuration = configuration;
        this.driver = driver;
    }

    /**
     * Attempts to establish a connection to a database by dynamically loading a driver from the provided JAR file.
     *
     * @return A connection object if connecting was successful
     * @throws DriverUnableToConnectException if no valid driver was found in the JAR file
     * @throws SQLException                   if an operation on the connection itself is invalid
     */
    @Override
    public Connection establishConnection() throws DriverUnableToConnectException, SQLException {
        try {
            Connection connection = driver.connect(configuration.getJDBCConnectionString(), configuration.toProperties());
            if (connection != null) return connection;
            throw new DriverUnableToConnectException("No suitable JDBC driver found in provided driver.");
        } catch (SQLException e) {
            throw SQLExceptionHandler.createActionableSQLException(e, "connection establishment");
        }
    }

}
