package configuration.model;

import db.connection.configuration.model.ConnectionConfiguration;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface ConfigurationTestTemplate {

    /**
     * Maps a configuration to the {@link Properties} it should produce when {@link Configuration#toProperties()} ()} is called.
     * @return {@link Stream} of {@link Arguments} which contain a {@link ConnectionConfiguration} and the {@link Properties} they are serialized to.
     */
    Stream<Arguments> mapConfigurationToProperties();

    @ParameterizedTest
    @MethodSource("mapConfigurationToProperties")
    default void testToProperties_withValidProperties_returnsProperties(Configuration configuration, Properties properties) {
        assertEquals(properties, configuration.toProperties());
    }
}
