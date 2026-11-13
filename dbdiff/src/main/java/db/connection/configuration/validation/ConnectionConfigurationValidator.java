package db.connection.configuration.validation;

import configuration.validation.ConfigurationValidator;
import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Properties;

public interface ConnectionConfigurationValidator extends ConfigurationValidator {

    static void requireValidConnectionAttributes(@NonNull Properties propertiesToCheck) throws MalformedConfigurationException {
        for (String key : propertiesToCheck.stringPropertyNames()) {
            if (Arrays.stream(ConnectionConfigurationAttribute.values())
                    .noneMatch(attribute -> attribute.key().equalsIgnoreCase(key))) {
                throw new MalformedConfigurationException(key + " is not a valid connection configuration attribute.");
            }
        }
    }

}
