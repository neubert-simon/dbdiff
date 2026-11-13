package db.connection.configuration.validation;

import db.exceptions.MalformedConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests in this class test static methods of the {@link ConnectionConfigurationValidator} interface.
 */
class ConnectionConfigurationValidatorTest {

    //region requireValidConnectionAttributes
    @Test
    void testRequireValidConnectionAttributes_withEmptyProperties_doesNotThrow() {
        assertDoesNotThrow(() ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(new Properties())
        );
    }

    @Test
    void testRequireValidConnectionAttributes_withValidAttributes_doesNotThrow() {
        Properties properties = new Properties();
        properties.setProperty("DATABASE", "POSTGRESQL");
        assertDoesNotThrow(() ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(properties)
        );
    }

    @Test
    void testRequireValidConnectionAttributes_withLowercaseAttribute_doesNotThrow() {
        Properties properties = new Properties();
        properties.setProperty("database", "POSTGRESQL");
        assertDoesNotThrow(() ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(properties)
        );
    }

    @Test
    void testRequireValidConnectionAttributes_withInvalidAttribute_throwsMalformedConfigurationException() {
        Properties properties = new Properties();
        properties.setProperty("INVALID_ATTRIBUTE", "value");
        assertThrows(MalformedConfigurationException.class, () ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(properties)
        );
    }

    @Test
    void testRequireValidConnectionAttributes_withMixedAttributes_throwsMalformedConfigurationException() {
        Properties properties = new Properties();
        properties.setProperty("DATABASE", "POSTGRESQL");
        properties.setProperty("INVALID_ATTRIBUTE", "value");
        assertThrows(MalformedConfigurationException.class, () ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(properties)
        );
    }

    @Test
    void testRequireValidConnectionAttributes_withNullProperties_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ConnectionConfigurationValidator.requireValidConnectionAttributes(null)
        );
    }
    //endregion

}
