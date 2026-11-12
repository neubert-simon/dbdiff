package configuration.validation;

import configuration.model.ConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import lombok.NonNull;

import java.util.*;

import static utility.ArgumentChecker.isBlank;

@FunctionalInterface
public interface ConfigurationValidator {

    /**
     * Validates connection configuration based on the internal state of the validator.
     * This method should be called before creating a {@link configuration.model.Configuration} instance.
     * This method normalizes properties, it does not alter or return the original {@link Properties}.
     *
     * @return The new validated and normalized properties.
     * @throws MalformedConfigurationException If the validation fails
     */
    Properties validate(@NonNull Properties properties) throws MalformedConfigurationException;

    /**
     * Argument checker for mandatory configuration attributes.
     * @param properties The key-value pairs to check.
     * @param attributes The attributes that need to exist in the provided {@link Properties}
     * @throws MalformedConfigurationException If there are missing attributes in the provided properties
     */
    static void requireMandatoryAttributes(@NonNull Properties properties, @NonNull Set<? extends ConfigurationAttribute> attributes) throws MalformedConfigurationException {
        if (attributes.isEmpty()) {
            throw new IllegalArgumentException("No mandatory attributes specified.");
        }

        if (properties.isEmpty()) throw new MalformedConfigurationException("No properties found in the provided configuration file.");

        Set<ConfigurationAttribute> missing = new HashSet<>();
        for (ConfigurationAttribute attribute : attributes) {
            if (!properties.containsKey(attribute.key()) || isBlank(properties.getProperty(attribute.key()))) {
                missing.add(attribute);
            }
        }

        if (!missing.isEmpty()) throw new MalformedConfigurationException("Missing required attributes: " + missing);
    }

}
