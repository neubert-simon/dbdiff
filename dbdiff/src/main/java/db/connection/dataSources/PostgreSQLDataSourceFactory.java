package db.connection.dataSources;

import db.connection.configs.ConnectionConfiguration;
import db.connection.configs.HostConfiguration;
import db.connection.configs.UrlConfiguration;
import lombok.NonNull;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class PostgreSQLDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(@NonNull ConnectionConfiguration connectionConfiguration) throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();

        switch (connectionConfiguration) {
            case UrlConfiguration cfg -> ds.setURL(JDBC_PREFIX + cfg.url());
            case HostConfiguration cfg -> {
                ds.setServerNames(new String[]{cfg.host()});
                ds.setDatabaseName(cfg.database());
                ds.setUser(cfg.username());
                ds.setPassword(cfg.password());
            }
            default -> throw new SQLException("Unknown connection configuration");
        }

        return ds;
    }

}
