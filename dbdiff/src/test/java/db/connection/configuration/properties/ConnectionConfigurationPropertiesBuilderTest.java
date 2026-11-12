package db.connection.configuration.properties;

import db.SupportedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.util.Properties;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionConfigurationPropertiesBuilderTest {

    private static final Path DRIVER_PATH = Path.of("drivers", "jdbc.jar");
    private static final URI URI_VALUE = URI.create("jdbc:postgresql://localhost:5432/test");
    private static final Path FILEPATH_VALUE = Path.of("data", "database.db");

    private ConnectionConfigurationPropertiesBuilder builder() {
        return new ConnectionConfigurationPropertiesBuilder(DRIVER_PATH);
    }

    private ConnectionConfigurationPropertiesBuilder validBuilder() {
        return builder().withUri(URI_VALUE);
    }

    @Test
    void testBuild_withoutConnectionType_throwsIllegalStateException() {
        assertThrows(
                IllegalStateException.class,
                () -> builder().build()
        );
    }

    @Test
    void testBuild_withOnlyOptionalFields_throwsIllegalStateException() {
        ConnectionConfigurationPropertiesBuilder builder = builder()
                .withHost("localhost")
                .withPort(5432)
                .withName("test")
                .withUsername("user")
                .withPassword("password");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @ParameterizedTest
    @EnumSource(SupportedDatabase.class)
    void testBuild_withDatabase_includesDatabaseAndDriver(SupportedDatabase database) {
        Properties properties = builder()
                .withDatabase(database)
                .build();

        assertAll(
                () -> assertEquals(
                        database.connectionStringPrefix,
                        properties.getProperty(DATABASE.key())
                ),
                () -> assertEquals(
                        DRIVER_PATH.toString(),
                        properties.getProperty(DRIVER.key())
                ),
                () -> assertEquals(2, properties.size())
        );
    }

    @Test
    void testBuild_withUri_includesUriAndDriver() {
        Properties properties = builder()
                .withUri(URI_VALUE)
                .build();

        assertAll(
                () -> assertEquals(
                        URI_VALUE.toString(),
                        properties.getProperty(URL.key())
                ),
                () -> assertEquals(
                        DRIVER_PATH.toString(),
                        properties.getProperty(DRIVER.key())
                ),
                () -> assertEquals(2, properties.size())
        );
    }

    @Test
    void testBuild_withFilepath_includesFilepathAndDriver() {
        Properties properties = builder()
                .withFilepath(FILEPATH_VALUE)
                .build();

        assertAll(
                () -> assertEquals(
                        FILEPATH_VALUE.toString(),
                        properties.getProperty(FILEPATH.key())
                ),
                () -> assertEquals(
                        DRIVER_PATH.toString(),
                        properties.getProperty(DRIVER.key())
                ),
                () -> assertEquals(2, properties.size())
        );
    }

    @Test
    void testBuild_withAllFields_includesEveryField() {
        SupportedDatabase database = SupportedDatabase.values()[0];

        Properties properties = builder()
                .withDatabase(database)
                .withUri(URI_VALUE)
                .withFilepath(FILEPATH_VALUE)
                .withHost("localhost")
                .withPort(5432)
                .withName("test")
                .withUsername("user")
                .withPassword("password")
                .build();

        assertAll(
                () -> assertEquals(
                        database.connectionStringPrefix,
                        properties.getProperty(DATABASE.key())
                ),
                () -> assertEquals(
                        URI_VALUE.toString(),
                        properties.getProperty(URL.key())
                ),
                () -> assertEquals(
                        FILEPATH_VALUE.toString(),
                        properties.getProperty(FILEPATH.key())
                ),
                () -> assertEquals("localhost",
                        properties.getProperty(HOST.key())),
                () -> assertEquals("5432",
                        properties.getProperty(PORT.key())),
                () -> assertEquals("test",
                        properties.getProperty(NAME.key())),
                () -> assertEquals("user",
                        properties.getProperty(USER.key())),
                () -> assertEquals("password",
                        properties.getProperty(PASSWORD.key())),
                () -> assertEquals(DRIVER_PATH.toString(),
                        properties.getProperty(DRIVER.key())),
                () -> assertEquals(9, properties.size())
        );
    }

    @Test
    void testBuild_withoutOptionalFields_omitsOptionalFields() {
        Properties properties = validBuilder().build();

        assertAll(
                () -> assertFalse(properties.containsKey(HOST.key())),
                () -> assertFalse(properties.containsKey(PORT.key())),
                () -> assertFalse(properties.containsKey(NAME.key())),
                () -> assertFalse(properties.containsKey(USER.key())),
                () -> assertFalse(properties.containsKey(PASSWORD.key()))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "localhost", "127.0.0.1"})
    void testBuild_withHost_preservesValue(String host) {
        Properties properties = validBuilder()
                .withHost(host)
                .build();

        assertEquals(host, properties.getProperty(HOST.key()));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 3306, 5432, 65535, Integer.MAX_VALUE})
    void testBuild_withPort_storesPortAsString(int port) {
        Properties properties = validBuilder()
                .withPort(port)
                .build();

        assertEquals(
                String.valueOf(port),
                properties.getProperty(PORT.key())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "test", "database_name"})
    void testBuild_withName_preservesValue(String name) {
        Properties properties = validBuilder()
                .withName(name)
                .build();

        assertEquals(name, properties.getProperty(NAME.key()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "admin", "test_user"})
    void testBuild_withUsername_preservesValue(String username) {
        Properties properties = validBuilder()
                .withUsername(username)
                .build();

        assertEquals(username, properties.getProperty(USER.key()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "secret", "p@ssw0rd!"})
    void testBuild_withPassword_preservesValue(String password) {
        Properties properties = validBuilder()
                .withPassword(password)
                .build();

        assertEquals(password, properties.getProperty(PASSWORD.key()));
    }

    @Test
    void testWithMethods_whenCalled_returnSameBuilder() {
        ConnectionConfigurationPropertiesBuilder builder = builder();

        assertAll(
                () -> assertSame(builder, builder.withDatabase(
                        SupportedDatabase.values()[0])),
                () -> assertSame(builder, builder.withUri(URI_VALUE)),
                () -> assertSame(builder, builder.withFilepath(FILEPATH_VALUE)),
                () -> assertSame(builder, builder.withHost("localhost")),
                () -> assertSame(builder, builder.withPort(5432)),
                () -> assertSame(builder, builder.withName("test")),
                () -> assertSame(builder, builder.withUsername("user")),
                () -> assertSame(builder, builder.withPassword("password"))
        );
    }

    @Test
    void testBuild_withRepeatedSetters_usesLatestValues() {
        Properties properties = validBuilder()
                .withHost("localhost")
                .withHost("127.0.0.1")
                .withPort(3306)
                .withPort(5432)
                .withName("first")
                .withName("second")
                .build();

        assertAll(
                () -> assertEquals("127.0.0.1",
                        properties.getProperty(HOST.key())),
                () -> assertEquals("5432",
                        properties.getProperty(PORT.key())),
                () -> assertEquals("second",
                        properties.getProperty(NAME.key()))
        );
    }

    @Test
    void testBuild_withNullOptionalFields_omitsFields() {
        Properties properties = validBuilder()
                .withHost("localhost")
                .withHost(null)
                .withUsername("user")
                .withUsername(null)
                .build();

        assertFalse(properties.containsKey(HOST.key()));
        assertFalse(properties.containsKey(USER.key()));
    }

    @Test
    void testBuild_afterClearingLastConnectionType_throwsIllegalStateException() {
        ConnectionConfigurationPropertiesBuilder builder = validBuilder();

        builder.withUri(null);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testBuild_whenCalledRepeatedly_returnsEqualButDistinctProperties() {
        ConnectionConfigurationPropertiesBuilder builder = validBuilder();

        Properties first = builder.build();
        Properties second = builder.build();

        assertNotSame(first, second);
        assertEquals(first, second);
    }

    @Test
    void testBuild_afterModifyingPreviousResult_remainsUnaffected() {
        ConnectionConfigurationPropertiesBuilder builder = validBuilder();

        Properties first = builder.build();
        first.setProperty(HOST.key(), "modified");
        first.remove(DRIVER.key());

        Properties second = builder.build();

        assertFalse(second.containsKey(HOST.key()));
        assertEquals(
                DRIVER_PATH.toString(),
                second.getProperty(DRIVER.key())
        );
    }

    @Test
    void testBuild_afterModifyingBuilder_preservesPreviousResult() {
        ConnectionConfigurationPropertiesBuilder builder = validBuilder()
                .withHost("localhost");

        Properties first = builder.build();

        builder.withHost("remote.example.com");
        Properties second = builder.build();

        assertEquals("localhost", first.getProperty(HOST.key()));
        assertEquals("remote.example.com", second.getProperty(HOST.key()));
    }

    @Test
    void testBuild_withAllFields_storesStringKeysAndValues() {
        Properties properties = builder()
                .withDatabase(SupportedDatabase.values()[0])
                .withUri(URI_VALUE)
                .withFilepath(FILEPATH_VALUE)
                .withHost("localhost")
                .withPort(5432)
                .withName("test")
                .withUsername("user")
                .withPassword("password")
                .build();

        properties.forEach((key, value) -> {
            assertInstanceOf(String.class, key);
            assertInstanceOf(String.class, value);
        });
    }

    @Test
    void testBuild_withAllFields_producesSerializableProperties() {
        Properties properties = builder()
                .withDatabase(SupportedDatabase.values()[0])
                .withUri(URI_VALUE)
                .withFilepath(FILEPATH_VALUE)
                .withHost("localhost")
                .withPort(5432)
                .withName("test")
                .withUsername("user")
                .withPassword("password")
                .build();

        assertDoesNotThrow(() -> {
            StringWriter writer = new StringWriter();
            properties.store(writer, null);
        });
    }
}