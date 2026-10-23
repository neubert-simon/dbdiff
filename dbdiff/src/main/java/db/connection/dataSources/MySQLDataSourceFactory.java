package db.connection.dataSources;

import com.mysql.cj.jdbc.MysqlDataSource;
import db.connection.configs.ConnectionConfiguration;
import db.connection.configs.HostConfiguration;
import db.connection.configs.UrlConfiguration;
import lombok.NonNull;

import javax.sql.DataSource;

public final class MySQLDataSourceFactory implements DataSourceFactory {

    @Override
    public DataSource createDataSource(@NonNull ConnectionConfiguration connectionConfiguration) {
        MysqlDataSource ds = new MysqlDataSource();

        switch (connectionConfiguration) {
            case UrlConfiguration c -> ds.setURL(c.url());

            case HostConfiguration c -> {
                ds.setServerName(c.host());
                ds.setPort(c.port());
                ds.setDatabaseName(c.database());
                ds.setUser(c.username());
                ds.setPassword(c.password());
            }
        }

        return ds;
    }
}