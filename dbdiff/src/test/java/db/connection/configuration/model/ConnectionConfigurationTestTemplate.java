package db.connection.configuration.model;

import configuration.model.ConfigurationTestTemplate;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface ConnectionConfigurationTestTemplate extends ConfigurationTestTemplate {

    /**
     * Maps a configuration to the driver path it has been instantiated with.
     * @return {@link Stream} of {@link Arguments} which contain a {@link ConnectionConfiguration} and the {@link Path} of the driver field it has been instantiated with.
     */
    Stream<Arguments> mapConfigurationToDriver();

    /**
     * Maps a configuration to the JDBC connection string it should produce when {@link ConnectionConfiguration#getJDBCConnectionString()} is called.
     * @return {@link Stream} of {@link Arguments} which contain a {@link ConnectionConfiguration} and the JDBC {@link String}.
     */
    Stream<Arguments> mapConfigurationToJDBCConnectionString();

    @ParameterizedTest
    @MethodSource("mapConfigurationToDriver")
    default void testGetDriver_withDriver_ReturnsDriverPath(ConnectionConfiguration configuration, Path driver) {
        assertEquals(driver, configuration.getDriver());
    }

    @ParameterizedTest
    @MethodSource("mapConfigurationToJDBCConnectionString")
    default void testGetJDBCConnectionString_withDriver_ReturnsDriverPath(ConnectionConfiguration configuration, String connectionString) {
        assertEquals(connectionString, configuration.getJDBCConnectionString());
    }

}
