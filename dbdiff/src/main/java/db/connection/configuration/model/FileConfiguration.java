package db.connection.configuration.model;

import db.SupportedDatabase;
import db.connection.configuration.properties.ConnectionConfigurationPropertiesBuilder;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import db.connection.configuration.properties.ConnectionConfigurationAttribute;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;

record FileConfiguration(
        @NonNull Path filepath,
        @NonNull Path driver
) implements ConnectionConfiguration {

    static final Set<ConnectionConfigurationAttribute> REQUIRED_ATTRIBUTES = Set.of(FILEPATH, DRIVER);

    @Override @NonNull
    public Path getDriver() {
        return driver;
    }

    @Override
    public Properties toProperties() {
        return new ConnectionConfigurationPropertiesBuilder(driver)
                .withFilepath(filepath)
                .build();
    }

    //region java.lang Overrides
    @Override
    public @NotNull String toString() {
        return JDBC_PREFIX + SupportedDatabase.SQLITE.connectionStringPrefix + ":" + filepath;
    }
    //endregion
}
