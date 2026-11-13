package db.connection.configuration.model;

import db.SupportedDatabase;
import db.connection.configuration.validation.ConnectionConfigurationAttributeValidator;
import db.connection.configuration.validation.ConnectionConfigurationTypeValidator;
import db.exceptions.MalformedConfigurationException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static db.connection.configuration.validation.ConnectionConfigurationTypeValidator.isRequiredPropertyValid;
import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;

@UtilityClass
public final class ConnectionConfigurationFactory {

    private final String UNKNOWN_CONFIG_MESSAGE = """
            Configuration file doesn't match any known configuration type.
            Please make sure you're configuration contains the necessary attributes.
            To learn more about connection configurations, run:
            dbdiff --config-formats
            """;

    private final String MUTUALLY_EXCLUSIVE_ATTRIBUTES_MESSAGE = "Configuration file contains multiple mutually exclusive attributes: ";

    /**
     * Turns a generic {@link Properties} object (which should be created from a config file) into a {@link ConnectionConfiguration}.
     *
     * @param properties The extracted properties representing the configuration.
     * @return The created {@link ConnectionConfiguration}
     * @throws MalformedConfigurationException if the configuration is invalid
     * @throws FileNotFoundException           if the driver can't be loaded
     */
    public static ConnectionConfiguration getConfigurationFromProperties(@NonNull Path propertySourceLocation, @NonNull Properties properties)
            throws MalformedConfigurationException, FileNotFoundException {
        try {
            ConnectionConfigurationType type = getConfigurationTypeFromProperties(properties);

            // Validate shape
            new ConnectionConfigurationTypeValidator(type).validate(properties);

            // Validate attribute values
            Properties normalized = new ConnectionConfigurationAttributeValidator(
                    type.requiredAttributes, propertySourceLocation
            ).validate(properties);

            return switch (type) {
                case HOST_CONFIGURATION -> createHostConfiguration(normalized);
                case URL_CONFIGURATION -> createUrlConfiguration(normalized);
                case FILE_CONFIGURATION -> createFileConfiguration(normalized);
            };

        } catch (InvalidPathException e) {
            throw new MalformedConfigurationException("Invalid path: " + e.getInput());
        }
    }

    private static ConnectionConfigurationType getConfigurationTypeFromProperties(Properties properties) throws MalformedConfigurationException {
        Set<ConnectionConfigurationType> types = new HashSet<>();

        // Host Configuration
        if (isRequiredPropertyValid(properties, HOST) || isRequiredPropertyValid(properties, PORT)) {
            types.add(ConnectionConfigurationType.HOST_CONFIGURATION);
        }

        // URL Configuration
        if (isRequiredPropertyValid(properties, URL)) {
            types.add(ConnectionConfigurationType.URL_CONFIGURATION);
        }

        // File Configuration
        if (isRequiredPropertyValid(properties, FILEPATH)) {
            types.add(ConnectionConfigurationType.FILE_CONFIGURATION);
        }

        if (types.isEmpty())
            throw new MalformedConfigurationException(UNKNOWN_CONFIG_MESSAGE); // TODO implement formatting help
        if (types.size() != 1)
            throw new MalformedConfigurationException(MUTUALLY_EXCLUSIVE_ATTRIBUTES_MESSAGE + types);

        return types.iterator().next();
    }

    private static ConnectionConfiguration createHostConfiguration(Properties properties) {
        return new HostConfiguration(
                SupportedDatabase.valueOf(properties.getProperty(DATABASE.key())),
                properties.getProperty(HOST.key()),
                Integer.parseInt(properties.getProperty(PORT.key())),
                properties.getProperty(NAME.key()),
                properties.getProperty(USER.key()),
                properties.getProperty(PASSWORD.key()),
                Path.of(properties.getProperty(DRIVER.key()))
        );
    }

    private static ConnectionConfiguration createUrlConfiguration(Properties properties) {
        return new UrlConfiguration(
                URI.create(properties.getProperty(URL.key())),
                properties.getProperty(USER.key()),
                properties.getProperty(PASSWORD.key()),
                Path.of(properties.getProperty(DRIVER.key()))
        );
    }

    private static ConnectionConfiguration createFileConfiguration(Properties properties) {
        return new FileConfiguration(
                Path.of(properties.getProperty(FILEPATH.key())),
                Path.of(properties.getProperty(DRIVER.key()))
        );
    }


}