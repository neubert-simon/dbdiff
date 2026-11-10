package db.driver;

import db.exceptions.DriverUnableToConnectException;

import java.sql.Driver;
import java.sql.SQLException;

public interface DriverLoader extends AutoCloseable {

    /**
     * Fetches driver based on the chosen dispatch mechanism.
     * @return A driver if the fetching mechanism was successful. Throws if not.
     * @throws DriverUnableToConnectException If a valid driver wasn't found.
     * @throws SQLException If a database error occurs during preliminary checks.
     */
    Driver load() throws DriverUnableToConnectException, SQLException;

}
