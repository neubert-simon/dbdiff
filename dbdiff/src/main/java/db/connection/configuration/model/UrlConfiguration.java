package db.connection.configuration.model;

import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import db.connection.configuration.properties.ConnectionConfigurationPropertiesBuilder;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static utility.ArgumentChecker.isBlank;

record UrlConfiguration(
        @NotBlank URI uri,
        @Nullable String username,
        @Nullable String password,
        @NonNull Path driver
) implements ConnectionConfiguration {

    static final Set<ConnectionConfigurationAttribute> REQUIRED_ATTRIBUTES = Set.of(URL, DRIVER);

    @Override
    @NonNull
    public Path getDriver() {
        return driver;
    }

    @Override
    public Properties toProperties() {
        return new ConnectionConfigurationPropertiesBuilder(driver)
                .withUri(uri)
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    //region java.lang Overrides
    @Override
    public @NotNull String toString() {
        String url = uri.toString();
        url = isBlank(username) ? url : url + "/" + username;
        url = isBlank(password) ? url : url + "/" + password;
        return url;
    }
    //endregion
}
