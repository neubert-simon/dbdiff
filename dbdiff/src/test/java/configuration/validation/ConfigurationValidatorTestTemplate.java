package configuration.validation;

import db.connection.configuration.validation.ConnectionConfigurationValidator;
import db.exceptions.MalformedConfigurationException;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class provides should be used to create {@link ConfigurationValidator} tests using the Template-Pattern.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface ConfigurationValidatorTestTemplate {

    /**
     * Provides valid properties to test.
     *
     * @return {@link Stream} of {@link Arguments} which map a valid {@link Properties} object to the expected normalized {@link Properties} resulting from a call to {@link ConfigurationValidator#validate(Properties)}.
     */
    Stream<Arguments> mapValidatorToValidProperties();

    /**
     * Provides invalid properties to test.
     *
     * @return {@link Stream} of {@link Properties} which should lead to failure.
     */
    Stream<Arguments> mapValidatorToInvalidProperties();

    Stream<ConnectionConfigurationValidator> validators();

    @ParameterizedTest
    @MethodSource("mapValidatorToValidProperties")
    default void testValidate_withValidProperties_ReturnsNormalizedProperties(
            ConfigurationValidator validator, Properties validProperties, Properties normalizedProperties
    ) {
        Properties result = assertDoesNotThrow(
                () -> validator.validate(validProperties)
        );
        assertEquals(normalizedProperties, result);
    }

    @ParameterizedTest
    @MethodSource("mapValidatorToInvalidProperties")
    default void testValidate_withInvalidProperties_throwsMalformedConfigurationException(
            ConfigurationValidator validator, Properties invalidProperties
    ) {
        assertThrows(
                MalformedConfigurationException.class,
                () -> validator.validate(invalidProperties)
        );
    }

    @ParameterizedTest
    @MethodSource("validators")
    default void testValidate_withEmptyProperties_throwsMalformedConfigurationException(ConfigurationValidator validator) {
        assertThrows(
                MalformedConfigurationException.class,
                () -> validator.validate(new Properties())
        );
    }

}
