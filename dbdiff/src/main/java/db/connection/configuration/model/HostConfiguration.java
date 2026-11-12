package db.connection.configuration.model;

import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import utility.ArgumentChecker;
import db.SupportedDatabase;
import db.connection.configuration.properties.ConnectionConfigurationPropertiesBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;

record HostConfiguration(
        @NonNull SupportedDatabase database,
        @NotBlank String host,
        @Positive int port,
        @Nullable String name,
        @Nullable String username,
        @Nullable String password,
        @NonNull Path driver
) implements ConnectionConfiguration {

    static final Set<ConnectionConfigurationAttribute> REQUIRED_ATTRIBUTES = Set.of(DATABASE, HOST, PORT, NAME, DRIVER);

    @Override @NonNull
    public Path getDriver() {
        return driver;
    }

    @Override
    public Properties toProperties() {
        return new ConnectionConfigurationPropertiesBuilder(driver)
                .withDatabase(database)
                .withHost(host)
                .withPort(port)
                .withName(name)
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    //region java.lang Overrides
    @Override
    public @NotNull String toString() {
        String s = JDBC_PREFIX + database.connectionStringPrefix + "://" + host + ":" + port + "/";
        return ArgumentChecker.isBlank(name) ? s : s + name;
    }
    //endregion

}