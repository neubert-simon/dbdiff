package db.connection.configuration.validation;

import configuration.validation.ConfigurationValidatorTestTemplate;

import static db.connection.configuration.model.ConnectionConfigurationType.*;
import static testUtility.fixtures.db.configuration.validation.ConnectionConfigurationValidationTestFixtures.*;

import db.connection.configuration.model.ConnectionConfigurationType;
import org.junit.jupiter.params.provider.Arguments;
import utility.StringUtils;

import java.util.*;
import java.util.stream.Stream;

class ConnectionConfigurationTypeValidatorTest implements ConfigurationValidatorTestTemplate {

    private final ConnectionConfigurationTypeValidator hostValidator =
            new ConnectionConfigurationTypeValidator(HOST_CONFIGURATION);
    private final ConnectionConfigurationTypeValidator urlValidator =
            new ConnectionConfigurationTypeValidator(URL_CONFIGURATION);
    private final ConnectionConfigurationTypeValidator fileValidator =
            new ConnectionConfigurationTypeValidator(FILE_CONFIGURATION);

    private final Set<ConnectionConfigurationTypeValidator> allValidators =
            new HashSet<>(List.of(hostValidator, urlValidator, fileValidator));

    //region Valid Properties
    @Override
    public Stream<Arguments> mapValidatorToValidProperties() {
        Set<Arguments> arguments = new HashSet<>();

        for (Properties p : validHostConfigurationProperties) {
            arguments.add(Arguments.of(hostValidator, p, StringUtils.transformPropertyKeys(p, String::toLowerCase)));
        }

        for (Properties p : validUrlConfigurationProperties) {
            arguments.add(Arguments.of(urlValidator, p, StringUtils.transformPropertyKeys(p, String::toLowerCase)));
        }

        for (Properties p : validFileConfigurationProperties) {
            arguments.add(Arguments.of(fileValidator, p, StringUtils.transformPropertyKeys(p, String::toLowerCase)));
        }

        return arguments.stream();
    }
    //endregion

    //region Invalid Properties
    @Override
    public Stream<Arguments> mapValidatorToInvalidProperties() {

        Set<Arguments> arguments = new HashSet<>();
        Set<Properties> invalidProperties = getInvalidGeneralProperties();

        for (ConnectionConfigurationTypeValidator validator : allValidators) {
            for (Properties invalidProperty : invalidProperties) {
                arguments.add(Arguments.of(validator, invalidProperty));
            }
        }

        for (Properties invalidProperty : createInvalidProperties(validHostConfigurationProperties)) {
            arguments.add(Arguments.of(hostValidator, invalidProperty));
        }

        for (Properties invalidProperty : createInvalidProperties(validUrlConfigurationProperties)) {
            arguments.add(Arguments.of(urlValidator, invalidProperty));
        }

        for (Properties invalidProperty : createInvalidProperties(validFileConfigurationProperties)) {
            arguments.add(Arguments.of(fileValidator, invalidProperty));
        }

        return arguments.stream();
    }
    //endregion

    @Override
    public Stream<ConnectionConfigurationValidator> validators() {
        return Arrays.stream(ConnectionConfigurationType.values())
                .map(ConnectionConfigurationTypeValidator::new);
    }
}
