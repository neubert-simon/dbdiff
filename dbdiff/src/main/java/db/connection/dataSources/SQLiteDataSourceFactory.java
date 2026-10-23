package db.connection.dataSources;

import db.connection.SupportedDbEngine;
import db.connection.configs.ConnectionConfiguration;
import db.connection.configs.HostConfiguration;
import db.connection.configs.UrlConfiguration;
import lombok.NonNull;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

public class SQLiteDataSourceFactory implements DataSourceFactory {

    private static final String SQLITE_PREFIX = "sqlite:";

    @Override
    public DataSource createDataSource(@NonNull ConnectionConfiguration connectionConfiguration) {
        return switch (connectionConfiguration) {
            case UrlConfiguration cfg -> createDataSourceFromUrl(cfg.url());
            case HostConfiguration cfg -> createDataSourceFromHostConfig(cfg);
        };
    }

    private DataSource createDataSourceFromUrl(String url) {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(createValidJdbcUrl(url, SupportedDbEngine.SQLITE));
        return ds;
    }

    private DataSource createDataSourceFromHostConfig(HostConfiguration cfg) {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl(createValidJdbcUrl(cfg.host(), SupportedDbEngine.SQLITE));
        ds.setDatabaseName(cfg.database());
        return ds;
    }

}
