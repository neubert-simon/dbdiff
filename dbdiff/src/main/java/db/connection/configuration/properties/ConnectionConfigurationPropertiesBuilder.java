package db.connection.configuration.properties;

import db.SupportedDatabase;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;

public class ConnectionConfigurationPropertiesBuilder {

    SupportedDatabase database;
    URI uri;
    Path filepath;

    String host;
    Integer port;
    String name;

    String username;
    String password;

    @NonNull
    Path driver;

    /**
     * Creates a builder for configuration {@link Properties}.
     *
     * @param driver The for the connection necessary driver.
     */
    public ConnectionConfigurationPropertiesBuilder(@NotNull Path driver) {
        this.driver = driver;
    }

    public ConnectionConfigurationPropertiesBuilder withDatabase(SupportedDatabase database) {
        this.database = database;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withFilepath(Path filepath) {
        this.filepath = filepath;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withHost(String host) {
        this.host = host;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public ConnectionConfigurationPropertiesBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Creates configuration {@link Properties}.
     */
    public Properties build() {
        Properties properties = new Properties();
        properties.put(DRIVER.key(), driver.toString());

        if (database == null && filepath == null && uri == null) {
            throw new IllegalStateException("Database, Filepath, and URL can not all be null in a connection configuration.");
        }

        if (database != null) properties.setProperty(DATABASE.key(), database.connectionStringPrefix);
        if (uri != null) properties.setProperty(URL.key(), uri.toString());
        if (filepath != null) properties.setProperty(FILEPATH.key(), filepath.toString());

        if (host != null) properties.setProperty(HOST.key(), host);
        if (port != null) properties.setProperty(PORT.key(), String.valueOf(port));
        if (name != null) properties.setProperty(NAME.key(), name);

        if (username != null) properties.setProperty(USER.key(), username);
        if (password != null) properties.setProperty(PASSWORD.key(), password);

        return properties;
    }
}
