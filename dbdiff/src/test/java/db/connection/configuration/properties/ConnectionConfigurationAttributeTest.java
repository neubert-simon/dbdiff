package db.connection.configuration.properties;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionConfigurationAttributeTest {

    @ParameterizedTest
    @EnumSource(ConnectionConfigurationAttribute.class)
    void testKey_whenCalled_returnsLowerCaseEnumName(ConnectionConfigurationAttribute attribute) {
        assertEquals(attribute.name().toLowerCase(), attribute.key());
    }

}
