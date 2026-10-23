package db.connection;

import db.connection.configs.ConnectionConfiguration;
import db.connection.dataSources.DataSourceFactory;
import db.connection.dataSources.MySQLDataSourceFactory;
import db.connection.dataSources.PostgreSQLDataSourceFactory;
import db.connection.dataSources.SQLiteDataSourceFactory;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.regex.Pattern;

@Getter
public enum SupportedDbEngine {
    POSTGRES("postgresql:", new PostgreSQLDataSourceFactory()),
    SQLITE("sqlite:", new SQLiteDataSourceFactory()),
    MYSQL("mysql", new MySQLDataSourceFactory())
    ;

    private final String connectionStringPrefix; // Consider changing to Pattern for regex checks
    private final DataSourceFactory dataSourceFactory;

    SupportedDbEngine(String connectionStringPrefix, DataSourceFactory factory) {
        this.connectionStringPrefix = connectionStringPrefix;
        this.dataSourceFactory = factory;
    }

    public DataSource createDataSource(ConnectionConfiguration config) throws SQLException {
        return dataSourceFactory.createDataSource(config);
    }

}
