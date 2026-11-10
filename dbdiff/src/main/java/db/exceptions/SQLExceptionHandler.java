package db.exceptions;

import java.sql.SQLException;

public class SQLExceptionHandler {

    public static SQLException createActionableSQLException(SQLException ex, String during) {
        String message = switch (ex.getSQLState()) {
            case "0700B" -> "Type conversion failure.";
            case "42000" -> ex.getMessage();
            case "08003" -> "The connection has already been closed";
            case "3D000" -> "Database name is required for this engine and wasn't found.";
            case "08S01" -> "Check that your database is online.";
            default -> "Unhandled SQL error: " + ex.getSQLState();
        };
        // TODO Test and implement
        return new SQLException(message + " Error occurred during " + during + ".");
    }

}
