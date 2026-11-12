package configuration.validation;

import configuration.model.ConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests in this class test static methods of {@link ConfigurationValidator}.
 */
class ConfigurationValidatorTest {

    //region requireMandatoryAttributes
    private enum TestAttribute implements ConfigurationAttribute {
        HOST, PORT, USER, PASSWORD;

        @Override
        public String key() {
            return name().toLowerCase();
        }
    }

    private static final Set<TestAttribute> REQUIRED = EnumSet.of(TestAttribute.HOST, TestAttribute.PORT);

    private Properties properties;

    @BeforeEach
    void setUp() {
        properties = new Properties();
        properties.setProperty(TestAttribute.HOST.key(), "localhost");
        properties.setProperty(TestAttribute.PORT.key(), "5432");
    }

    private void validate(Set<? extends ConfigurationAttribute> required) throws MalformedConfigurationException {
        ConfigurationValidator.requireMandatoryAttributes(properties, required);
    }

    @Test
    void validPropertiesShouldPass() {
        assertDoesNotThrow(() -> validate(REQUIRED));
    }

    @Test
    void emptyPropertiesShouldThrow() {
        properties.clear();
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
    }

    @ParameterizedTest
    @EnumSource(value = TestAttribute.class, names = {"HOST", "PORT"})
    void missingRequiredAttributeShouldThrow(TestAttribute attribute) {
        properties.remove(attribute.key());
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n", "\r\n"})
    void blankRequiredAttributeShouldThrow(String value) {
        properties.setProperty(TestAttribute.HOST.key(), value);
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
    }

    @Test
    void multipleMissingAttributesShouldThrow() {
        properties.remove(TestAttribute.HOST.key());
        properties.remove(TestAttribute.PORT.key());
        properties.setProperty("unrelated", "value");
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
    }

    @Test
    void mixedMissingAndBlankAttributesShouldThrow() {
        properties.remove(TestAttribute.HOST.key());
        properties.setProperty(TestAttribute.PORT.key(), " ");
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
    }

    @Test
    void optionalAttributesMayBeAbsent() {
        assertDoesNotThrow(() -> validate(REQUIRED));
    }

    @Test
    void optionalAttributesMayBeBlank() {
        properties.setProperty(TestAttribute.USER.key(), "");
        properties.setProperty(TestAttribute.PASSWORD.key(), " ");
        assertDoesNotThrow(() -> validate(REQUIRED));
    }

    @Test
    void additionalPropertiesShouldBeIgnored() {
        properties.setProperty("unexpected", "value");
        assertDoesNotThrow(() -> validate(REQUIRED));
    }

    @Test
    void emptyRequiredSetShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> validate(Set.of()));
    }

    @Test
    void emptyPropertiesAndEmptyRequiredSetShouldThrow() {
        properties.clear();
        assertThrows(IllegalArgumentException.class, () -> validate(Set.of()));
    }

    @Test
    void onlySpecifiedAttributesShouldBeValidated() {
        properties.clear();
        properties.setProperty(TestAttribute.HOST.key(), "localhost");
        assertDoesNotThrow(() -> validate(Set.of(TestAttribute.HOST)));
    }

    @Test
    void validPropertiesShouldNotBeModified() {
        Properties original = new Properties();
        original.putAll(properties);
        assertDoesNotThrow(() -> validate(REQUIRED));
        assertEquals(original, properties);
    }

    @Test
    void invalidPropertiesShouldNotBeModified() {
        properties.remove(TestAttribute.HOST.key());
        Properties original = new Properties();
        original.putAll(properties);
        assertThrows(MalformedConfigurationException.class, () -> validate(REQUIRED));
        assertEquals(original, properties);
    }
    //endregion
}
