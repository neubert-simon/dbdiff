package db.connection.configuration.validation;

import configuration.model.ConfigurationAttribute;
import db.connection.configuration.model.ConnectionConfigurationType;
import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static utility.ArgumentChecker.isBlank;
import static utility.ArgumentChecker.requireNonBlank;

@RequiredArgsConstructor
public final class ConnectionConfigurationTypeValidator implements ConnectionConfigurationValidator {

    private final ConnectionConfigurationType configurationType;

    @Override
    public Properties validate(@NonNull Properties properties) throws MalformedConfigurationException {
        Properties normalized = utility.StringUtils.transformPropertyKeys(properties, String::toLowerCase);
        ConnectionConfigurationValidator.requireValidConnectionAttributes(normalized);
        validateDriver(normalized);
        validateConfigurationShape(normalized, configurationType);
        validateCredentials(normalized);
        return normalized;
    }

    //region Validation
    private void validateDriver(Properties properties) throws MalformedConfigurationException {
        if(!isRequiredPropertyValid(properties, DRIVER)) {
            throw new MalformedConfigurationException("Required property " + DRIVER.key() + " is missing.");
        }
    }

    private void validateCredentials(Properties properties) throws MalformedConfigurationException {
        List<String> credentialKeys = List.of(USER.key(), PASSWORD.key());
        try {
            if (properties.stringPropertyNames().stream().map(String::toLowerCase).anyMatch(credentialKeys::contains)) {
                requireAllPropertiesPresentTogether(properties, Set.of(USER, PASSWORD));
                requireNonBlank(properties.getProperty(USER.key()), USER.key());
                requireNonBlank(properties.getProperty(PASSWORD.key()), PASSWORD.key());
            }
        } catch (IllegalArgumentException e) {
            throw new MalformedConfigurationException(e.getMessage());
        }
    }

    private void validateConfigurationShape(Properties properties, ConnectionConfigurationType type) throws MalformedConfigurationException {
        List<ConnectionConfigurationAttribute> invalidRequiredAttributes = new ArrayList<>();

        for (ConnectionConfigurationAttribute attribute : type.requiredAttributes) {
            if(!isRequiredPropertyValid(properties, attribute)) invalidRequiredAttributes.add(attribute);
        }

        if (!invalidRequiredAttributes.isEmpty()) {
            throw new MalformedConfigurationException(
                    "Missing required attributes for " + type + ": " + invalidRequiredAttributes
            );
        }
    }
    //endregion

    //region Auxiliary
    public static boolean isRequiredPropertyValid(Properties properties, ConnectionConfigurationAttribute attribute) {
        return !(isBlank(properties.getProperty(attribute.key())) && isBlank(properties.getProperty(attribute.name())));
    }

    private static void requireAllPropertiesPresentTogether(Properties properties, Set<? extends ConfigurationAttribute> attributes) throws MalformedConfigurationException {
        Set<String> keys = properties.stringPropertyNames();
        Set<String> attributeKeys = attributes.stream().map(ConfigurationAttribute::key).collect(Collectors.toSet());

        if (keys.stream().anyMatch(attributeKeys::contains) && !keys.containsAll(attributeKeys)) {
            throw new MalformedConfigurationException(
                    "These properties must all be present if at least one of them was provided: "
                    + attributes.stream().map(a -> StringUtils.capitalize(a.key())).toList()
            );
        }
    }
    //endregion

}
