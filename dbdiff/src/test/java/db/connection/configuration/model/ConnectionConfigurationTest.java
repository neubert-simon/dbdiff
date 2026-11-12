package db.connection.configuration.model;

import db.SupportedDatabase;
import db.connection.configuration.properties.ConnectionConfigurationAttribute;

import static db.connection.configuration.properties.ConnectionConfigurationAttribute.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static testUtility.fixtures.db.configuration.model.ConnectionConfigurationTestFixtures.*;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.provider.Arguments;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

public class ConnectionConfigurationTest implements ConnectionConfigurationTestTemplate {

    final static String PREFIX = "jdbc:";

    @Override
    public Stream<Arguments> mapConfigurationToProperties() {
        return Stream.of(
                Arguments.of(fixtureToHostConfiguration(validHostConnection1), createPropertiesFromFixture(validHostConnection1)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection2), createPropertiesFromFixture(validHostConnection2)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection3), createPropertiesFromFixture(validHostConnection3)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection4), createPropertiesFromFixture(validHostConnection4)),

                Arguments.of(fixtureToUrlConfiguration(validUrlConnection1), createPropertiesFromFixture(validUrlConnection1)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection2), createPropertiesFromFixture(validUrlConnection2)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection3), createPropertiesFromFixture(validUrlConnection3)),

                Arguments.of(fixtureToFileConfiguration(validFileConnection1), createPropertiesFromFixture(validFileConnection1)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection2), createPropertiesFromFixture(validFileConnection2)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection3), createPropertiesFromFixture(validFileConnection3))

        );
    }

    @Override
    public Stream<Arguments> mapConfigurationToDriver() {
        return Stream.of(
                Arguments.of(fixtureToHostConfiguration(validHostConnection1), validHostConnection1.get(DRIVER)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection2), validHostConnection2.get(DRIVER)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection3), validHostConnection3.get(DRIVER)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection4), validHostConnection4.get(DRIVER)),

                Arguments.of(fixtureToUrlConfiguration(validUrlConnection1), validUrlConnection1.get(DRIVER)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection2), validUrlConnection2.get(DRIVER)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection3), validUrlConnection3.get(DRIVER)),

                Arguments.of(fixtureToFileConfiguration(validFileConnection1), validFileConnection1.get(DRIVER)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection2), validFileConnection2.get(DRIVER)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection3), validFileConnection3.get(DRIVER))

        );
    }

    @Override
    public Stream<Arguments> mapConfigurationToJDBCConnectionString() {
        return assertDoesNotThrow(() -> Stream.of(
                Arguments.of(fixtureToHostConfiguration(validHostConnection1), getExpectedJDBCUrlForHostConfiguration(validHostConnection1)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection2), getExpectedJDBCUrlForHostConfiguration(validHostConnection2)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection3), getExpectedJDBCUrlForHostConfiguration(validHostConnection3)),
                Arguments.of(fixtureToHostConfiguration(validHostConnection4), getExpectedJDBCUrlForHostConfiguration(validHostConnection4)),

                Arguments.of(fixtureToUrlConfiguration(validUrlConnection1), validUrlConnection1.get(URL) + "/" + validUrlConnection1.get(USER) + "/" + validUrlConnection1.get(PASSWORD)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection2), validUrlConnection2.get(URL)),
                Arguments.of(fixtureToUrlConfiguration(validUrlConnection3), validUrlConnection3.get(URL)),

                Arguments.of(fixtureToFileConfiguration(validFileConnection1), getExpectedJDBCUrlForFileConfiguration(validFileConnection1)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection2), getExpectedJDBCUrlForFileConfiguration(validFileConnection2)),
                Arguments.of(fixtureToFileConfiguration(validFileConnection3), getExpectedJDBCUrlForFileConfiguration(validFileConnection3))
        ));
    }

    private static @NonNull String getExpectedJDBCUrlForHostConfiguration(Map<ConnectionConfigurationAttribute, String> fixture) {
        return PREFIX + fixture.get(DATABASE) + "://" + fixture.get(HOST) + ":" + fixture.get(PORT) + "/" + fixture.get(NAME);
    }

    private static @NonNull String getExpectedJDBCUrlForFileConfiguration(Map<ConnectionConfigurationAttribute, String> fixture) {
        return PREFIX + "sqlite:" + fixture.get(FILEPATH);
    }

    static ConnectionConfiguration fixtureToHostConfiguration(Map<ConnectionConfigurationAttribute, String> fixture) {
        return new HostConfiguration(
                assertDoesNotThrow(() -> SupportedDatabase.getFromProductName(fixture.get(DATABASE))),
                fixture.get(HOST),
                Integer.parseInt(fixture.get(PORT)),
                fixture.get(NAME),
                fixture.get(USER),
                fixture.get(PASSWORD),
                Path.of(fixture.get(DRIVER))
        );
    }

    static ConnectionConfiguration fixtureToUrlConfiguration(Map<ConnectionConfigurationAttribute, String> fixture) {
        return new UrlConfiguration(
                URI.create(fixture.get(URL)),
                fixture.get(USER),
                fixture.get(PASSWORD),
                Path.of(fixture.get(DRIVER))
        );
    }

    static ConnectionConfiguration fixtureToFileConfiguration(Map<ConnectionConfigurationAttribute, String> fixture) {
        return new FileConfiguration(
                Path.of(fixture.get(FILEPATH)),
                Path.of(fixture.get(DRIVER))
        );
    }

}
