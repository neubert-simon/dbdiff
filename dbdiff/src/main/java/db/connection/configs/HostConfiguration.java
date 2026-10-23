package db.connection.configs;

public record HostConfiguration(
        String host,
        int port,
        String database,
        String username,
        String password
) implements ConnectionConfiguration {
}