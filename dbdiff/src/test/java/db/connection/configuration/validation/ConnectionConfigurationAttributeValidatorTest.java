package db.connection.configuration.validation;

import configuration.validation.ConfigurationValidatorTestTemplate;
import db.SupportedDatabase;
import db.connection.configuration.model.ConnectionConfigurationType;
import db.exceptions.UnsupportedDatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import utility.ArgumentChecker;
import utility.StringUtils;
import utility.SystemUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static db.connection.configuration.model.ConnectionConfigurationType.*;
import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static testUtility.fixtures.db.configuration.validation.ConnectionConfigurationValidationTestFixtures.*;

public class ConnectionConfigurationAttributeValidatorTest implements ConfigurationValidatorTestTemplate {

    private MockedStatic<SystemUtils> systemUtilsMock;
    private MockedStatic<ArgumentChecker> argumentCheckerMock;

    private final ConnectionConfigurationAttributeValidator hostValidator = assertDoesNotThrow(() -> new ConnectionConfigurationAttributeValidator(HOST_CONFIGURATION.requiredAttributes, validPropertyLocation));
    private final ConnectionConfigurationAttributeValidator urlValidator = assertDoesNotThrow(() -> new ConnectionConfigurationAttributeValidator(URL_CONFIGURATION.requiredAttributes, validPropertyLocation));
    private final ConnectionConfigurationAttributeValidator fileValidator = assertDoesNotThrow(() -> new ConnectionConfigurationAttributeValidator(FILE_CONFIGURATION.requiredAttributes, validPropertyLocation));
    private final Set<ConnectionConfigurationAttributeValidator> allValidators = new HashSet<>(Arrays.asList(hostValidator, urlValidator, fileValidator));

    private final String validConfigLocation = "/valid.cfg";

    @BeforeEach
    public void setUp() {
        systemUtilsMock = Mockito.mockStatic(SystemUtils.class, Mockito.CALLS_REAL_METHODS);
        systemUtilsMock.when(() -> SystemUtils.relativeToAbsoluteFilepath(eq(validPropertyLocation), any())).thenReturn(Path.of(validConfigLocation));
        argumentCheckerMock = Mockito.mockStatic(ArgumentChecker.class, Mockito.CALLS_REAL_METHODS);
        argumentCheckerMock.when(() -> ArgumentChecker.requireLeafExists(Path.of(validConfigLocation))).thenAnswer((Answer<Void>) _ -> null);
        argumentCheckerMock.when(() -> ArgumentChecker.requireLeafExists(validFileLocation)).thenAnswer((Answer<Void>) _ -> null);
    }

    @AfterEach
    public void tearDown() {
        systemUtilsMock.close();
        argumentCheckerMock.close();
    }

    @Override
    public Stream<Arguments> mapValidatorToValidProperties() {
        Set<Arguments> arguments = new HashSet<>();

        for (Properties p : validHostConfigurationProperties) {
            arguments.add(Arguments.of(hostValidator, p, assertDoesNotThrow(() -> normalizeProperties(p))));
        }

        for (Properties p : validUrlConfigurationProperties) {
            arguments.add(Arguments.of(urlValidator, p, assertDoesNotThrow(() -> normalizeProperties(p))));
        }

        for (Properties p : validFileConfigurationProperties) {
            arguments.add(Arguments.of(fileValidator, p, assertDoesNotThrow(() -> normalizeProperties(p))));
        }

        return arguments.stream();
    }

    @Override
    public Stream<Arguments> mapValidatorToInvalidProperties() {
        Set<Arguments> arguments = new HashSet<>();
        Set<Properties> invalidProperties = getInvalidGeneralProperties();

        for (ConnectionConfigurationAttributeValidator validator : allValidators) {
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

    @Override
    public Stream<ConnectionConfigurationValidator> validators() {
        return Arrays.stream(ConnectionConfigurationType.values())
                .map((c) -> assertDoesNotThrow(
                        () -> new ConnectionConfigurationAttributeValidator(
                                c.requiredAttributes, validPropertyLocation
                        ))
                );
    }

    private Properties normalizeProperties(Properties p) throws UnsupportedDatabaseException {
        p = StringUtils.transformPropertyKeys(p, String::toLowerCase);
        p.setProperty(DRIVER.key(), validConfigLocation);
        String name = p.getProperty(DATABASE.key());
        if (name != null) p.setProperty(DATABASE.key(), SupportedDatabase.getFromName(name).name());
        return p;
    }
}
