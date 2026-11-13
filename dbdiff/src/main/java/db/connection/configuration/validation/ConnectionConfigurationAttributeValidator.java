package db.connection.configuration.validation;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static utility.ArgumentChecker.*;

import configuration.validation.ConfigurationValidator;
import db.SupportedDatabase;
import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import db.exceptions.UnsupportedDatabaseException;
import lombok.NonNull;
import utility.StringUtils;
import utility.SystemUtils;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

public class ConnectionConfigurationAttributeValidator implements ConnectionConfigurationValidator {

    private final Set<ConnectionConfigurationAttribute> requiredAttributes;
    private final Path propertyOrigin;

    public ConnectionConfigurationAttributeValidator(Set<ConnectionConfigurationAttribute> requiredAttributes, Path propertyOrigin) throws FileNotFoundException {
        requireLeafExists(propertyOrigin);
        requireNoNullElements(requiredAttributes);
        this.propertyOrigin = propertyOrigin;
        this.requiredAttributes = requiredAttributes;
    }

    @Override
    public Properties validate(@NonNull Properties properties) throws MalformedConfigurationException {
        Properties normalized = StringUtils.transformPropertyKeys(properties, String::toLowerCase);

        ConnectionConfigurationValidator.requireValidConnectionAttributes(normalized);
        ConfigurationValidator.requireMandatoryAttributes(normalized, requiredAttributes);

        for (ConnectionConfigurationAttribute attribute : ConnectionConfigurationAttribute.values()) {
            //if (!requiredAttributes.contains(attribute)) continue;
            if (!normalized.containsKey(attribute.key())) {continue;}
            try {
                switch (attribute) {
                    case DRIVER -> normalized.setProperty(DRIVER.key(), validateDriver(normalized).toString());
                    case DATABASE -> normalized.setProperty(DATABASE.key(), validateDatabase(normalized).name());
                    case USER -> normalized.setProperty(USER.key(), validateUser(normalized));
                    case PASSWORD -> normalized.setProperty(PASSWORD.key(), validatePassword(normalized));
                    case HOST -> normalized.setProperty(HOST.key(), validateHost(normalized));
                    case PORT -> normalized.setProperty(PORT.key(), String.valueOf(validatePort(normalized)));
                    case NAME -> normalized.setProperty(NAME.key(), validateDatabaseName(normalized));
                    case URL -> normalized.setProperty(URL.key(), validateUri(normalized).toString());
                    case FILEPATH -> normalized.setProperty(FILEPATH.key(), validatePath(normalized).toString());
                }
            } catch (Exception e) {
                throw new MalformedConfigurationException(e.getMessage());
            }
        }

        return normalized;
    }

    private Path validateDriver(@NonNull Properties properties) throws FileNotFoundException {
        String driverPath = properties.getProperty(DRIVER.key());
        requireNonBlank(driverPath, DRIVER.key());

        Path driver = SystemUtils.relativeToAbsoluteFilepath(propertyOrigin, Path.of(driverPath));
        requireLeafExists(driver);

        return driver;
    }

    private SupportedDatabase validateDatabase(@NonNull Properties properties) throws UnsupportedDatabaseException {
        String db = properties.getProperty(DATABASE.key());
        SupportedDatabase supportedDatabase;
        try {
            supportedDatabase = SupportedDatabase.getFromProductName(db);
        } catch (UnsupportedDatabaseException e) {
            supportedDatabase = SupportedDatabase.getFromName(db);
        }
        return supportedDatabase;
    }

    private URI validateUri(@NonNull Properties properties) throws URISyntaxException {
        return new URI(properties.getProperty(URL.key()));
    }

    private Path validatePath(@NonNull Properties properties) throws FileNotFoundException {
        Path path = Path.of(properties.getProperty(FILEPATH.key()));
        requireLeafExists(path);
        return path;
    }

    private String validateHost(@NonNull Properties properties) {
        String host = properties.getProperty(HOST.key());
        requireNonBlank(host, HOST.key());
        return host;
    }

    private int validatePort(Properties properties) throws MalformedConfigurationException {
        String value = properties.getProperty(PORT.key());
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
            return port;
        } catch (NumberFormatException e) {
            throw new MalformedConfigurationException("Invalid port: " + value + ". Must a number between 1 and 65535.");
        }
    }

    private String validateDatabaseName(@NonNull Properties properties) {
        String databaseName = properties.getProperty(NAME.key());
        requireNonBlank(databaseName, NAME.key());
        return databaseName;
    }

    private String validateUser(@NonNull Properties properties) {
        String user = properties.getProperty(USER.key());
        requireNonBlank(user, "DB user");
        return user;
    }

    private String validatePassword(@NonNull Properties properties) {
        String user = properties.getProperty(PASSWORD.key());
        requireNonBlank(user, "DB password");
        return user;
    }
}
