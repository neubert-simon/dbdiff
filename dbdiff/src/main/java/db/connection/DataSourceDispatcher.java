package db.connection;

import db.connection.configs.HostConfiguration;
import db.connection.configs.UrlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceDispatcher {

    public static Connection connectDataSource(HostConfiguration cfg) throws SQLException {
        var engine = SupportedDbEngine.POSTGRES;
        return engine.getDataSourceFactory().createDataSource(cfg).getConnection();
    }

    public static Connection connectDataSource(String url) throws SQLException {
        var engine = getEngineFromConnectionString(url);
        return engine.getDataSourceFactory().createDataSource(new UrlConfiguration(url)).getConnection();
    }

    private static SupportedDbEngine getEngineFromConnectionString(String url) {
        for (SupportedDbEngine e : SupportedDbEngine.values()) {
            if (url.startsWith(e.getConnectionStringPrefix())) {
                return e;
            }
        }

        if (new File(url).exists()) {
            return SupportedDbEngine.SQLITE;
        }

        throw new UnsupportedDatabaseException("Unsupported url: " + url);

    }

}
