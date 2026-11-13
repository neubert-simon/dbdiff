package db.connection.configuration.model;

import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import db.exceptions.MalformedConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import utility.ArgumentChecker;
import utility.SystemUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import static db.connection.configuration.model.ConnectionConfigurationType.*;
import static testUtility.fixtures.db.configuration.model.ConnectionConfigurationTestFixtures.createPropertiesFromFixture;
import static testUtility.fixtures.db.configuration.model.ConnectionConfigurationTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConnectionConfigurationFactoryTest {

    private final Path ORIGIN = Path.of("/");

    private MockedStatic<SystemUtils> systemUtilsMock;
    private MockedStatic<ArgumentChecker> argumentCheckerMock;

    @BeforeEach
    void setUp() {
        systemUtilsMock = Mockito.mockStatic(SystemUtils.class, Mockito.CALLS_REAL_METHODS);
        systemUtilsMock.when(() -> SystemUtils.relativeToAbsoluteFilepath(eq(ORIGIN), any())).thenReturn(ORIGIN);
        argumentCheckerMock = Mockito.mockStatic(ArgumentChecker.class, Mockito.CALLS_REAL_METHODS);
        argumentCheckerMock.when(() -> ArgumentChecker.requireLeafExists((Path) any())).thenAnswer((Answer<Void>) _ -> null);
    }

    @AfterEach
    void tearDown() {
        systemUtilsMock.close();
        argumentCheckerMock.close();
    }

    private Stream<Arguments> hostFixtures() {

        return Stream.of(
                getArguments(validHostConnection1, HOST_CONFIGURATION),
                getArguments(validHostConnection2, HOST_CONFIGURATION),
                getArguments(validHostConnection3, HOST_CONFIGURATION),
                getArguments(validHostConnection4, HOST_CONFIGURATION),

                getArguments(validUrlConnection1, URL_CONFIGURATION),
                getArguments(validUrlConnection2, URL_CONFIGURATION),
                getArguments(validUrlConnection3, URL_CONFIGURATION),

                getArguments(validFileConnection1, FILE_CONFIGURATION),
                getArguments(validFileConnection2, FILE_CONFIGURATION),
                getArguments(validFileConnection3, FILE_CONFIGURATION)
        );
    }

    private Arguments getArguments(Map<ConnectionConfigurationAttribute, String> fixture, ConnectionConfigurationType type) {
        Function<Map<ConnectionConfigurationAttribute, String>, ConnectionConfiguration> factory = switch (type) {
            case HOST_CONFIGURATION -> ConnectionConfigurationTest::fixtureToHostConfiguration;
            case URL_CONFIGURATION -> ConnectionConfigurationTest::fixtureToUrlConfiguration;
            case FILE_CONFIGURATION -> ConnectionConfigurationTest::fixtureToFileConfiguration;
        };
        return Arguments.of(new HashMap<>(fixture), factory);
    }

    @ParameterizedTest
    @MethodSource("hostFixtures")
    void testGetConfigurationFromProperties_withHostProperties_returnsCorrectInstance(HashMap<ConnectionConfigurationAttribute, String> fixture, Function<Map<ConnectionConfigurationAttribute, String>, ConnectionConfiguration> fixtureToConfiguration) {
        fixture.replace(ConnectionConfigurationAttribute.DRIVER, ORIGIN.toString());
        ConnectionConfiguration config = fixtureToConfiguration.apply(fixture);
        assertEquals(
                config,
                assertDoesNotThrow(
                        () -> ConnectionConfigurationFactory.getConfigurationFromProperties(
                                ORIGIN,
                                createPropertiesFromFixture(fixture)
                        )
                )
        );
    }

    @Test
    void testGetConfigurationFromProperties_withEmptyProperties_throwsMalformedConfigurationException() {
        assertThrows(MalformedConfigurationException.class, () -> ConnectionConfigurationFactory.getConfigurationFromProperties(ORIGIN, new Properties()));
    }

    @Test
    void testGetConfigurationFromProperties_withEmptyPropertyValues_throwsMalformedConfigurationException() {
        Properties emptyProperties = new Properties();
        Arrays.stream(ConnectionConfigurationAttribute.values()).forEach(att -> emptyProperties.setProperty(att.key(), ""));
        assertThrows(MalformedConfigurationException.class, () -> ConnectionConfigurationFactory.getConfigurationFromProperties(ORIGIN, emptyProperties));
    }

    @Test
    void testGetConfigurationFromProperties_withNullProperties_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ConnectionConfigurationFactory.getConfigurationFromProperties(ORIGIN, null));
    }

    @Test
    void testGetConfigurationFromProperties_withNullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ConnectionConfigurationFactory.getConfigurationFromProperties(null, new Properties()));
    }


}
