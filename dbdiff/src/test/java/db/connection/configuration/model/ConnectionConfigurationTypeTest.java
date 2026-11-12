package db.connection.configuration.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectionConfigurationTypeTest {

    @Test
    void testToString_forType_returnsProperlyFormattedName() {
        assertEquals("Host Configuration", ConnectionConfigurationType.HOST_CONFIGURATION.toString());
        assertEquals("Url Configuration", ConnectionConfigurationType.URL_CONFIGURATION.toString());
        assertEquals("File Configuration", ConnectionConfigurationType.FILE_CONFIGURATION.toString());
    }

}
