package db.connection;

import db.connection.configuration.model.ConnectionConfiguration;
import db.exceptions.DriverUnableToConnectException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A {@link ConnectionDispatcher} attempts to load a connection to a database via a dynamically loaded {@link java.sql.Driver}.
 */
public interface ConnectionDispatcher {

    /**
     * Attempts to establish a {@link Connection} via the provided {@link ConnectionConfiguration}.
     * @return A {@link Connection} to the database if the {@link ConnectionConfiguration} was valid.
     * @throws SQLException If operations on the connection fail.
     * @throws DriverUnableToConnectException If the connection itself couldn't be established.
     */
    Connection establishConnection() throws SQLException, DriverUnableToConnectException;

}
