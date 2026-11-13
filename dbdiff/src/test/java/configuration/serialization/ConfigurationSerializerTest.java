package configuration.serialization;


import db.connection.configuration.model.ConnectionConfiguration;
import db.exceptions.MalformedConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigurationSerializerTest {

    @TempDir
    Path tempDir;

    @Test
    void testSerialize_validConfiguration_writesPropertiesToFile() throws IOException {
        Path file = tempDir.resolve("config.cfg");

        Properties properties = new Properties();
        properties.setProperty("host", "localhost");
        properties.setProperty("port", "5432");
        properties.setProperty("user", "test");

        ConnectionConfiguration config = mock(ConnectionConfiguration.class);
        when(config.toProperties()).thenReturn(properties);

        ConfigurationSerializer.serialize(file, config);
        assertTrue(Files.isRegularFile(file));

        Properties result = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            result.load(in);
        }

        assertEquals("localhost", result.getProperty("HOST"));
        assertEquals("5432", result.getProperty("PORT"));
        assertEquals("test", result.getProperty("USER"));
        assertEquals(3, result.size());
    }

    @Test
    void testSerialize_nonexistentParentDirectory_throwsIOException() {
        Path file = tempDir.resolve("nonexistent/config.cfg");
        ConnectionConfiguration config = mock(ConnectionConfiguration.class);
        assertThrows(IOException.class, () -> ConfigurationSerializer.serialize(file, config));
    }

    @Test
    void testSerialize_nullPath_throwsNullPointerException() {
        ConnectionConfiguration config = mock(ConnectionConfiguration.class);
        assertThrows(NullPointerException.class, () -> ConfigurationSerializer.serialize(null, config));
    }

    @Test
    void testSerialize_nullConfiguration_throwsNullPointerException() {
        Path file = tempDir.resolve("config.cfg");
        assertThrows(NullPointerException.class, () -> ConfigurationSerializer.serialize(file, null));
    }

    @Test
    void testSerialize_existingFile_overwritesFile() throws IOException {
        Path file = tempDir.resolve("config.cfg");
        Files.writeString(file, "OLD=value\n");

        Properties properties = new Properties();
        properties.setProperty("host", "localhost");

        ConnectionConfiguration config = mock(ConnectionConfiguration.class);
        when(config.toProperties()).thenReturn(properties);

        ConfigurationSerializer.serialize(file, config);

        Properties result = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            result.load(in);
        }

        assertEquals("localhost", result.getProperty("HOST"));
        assertNull(result.getProperty("OLD"));
        assertEquals(1, result.size());
    }

    @Test
    void testDeserialize_validFile_returnsLowercaseProperties() throws IOException, MalformedConfigurationException {
        Path file = tempDir.resolve("config.cfg");

        Files.writeString(file, """
                HOST=localhost
                PORT=5432
                USER=test
                """);

        Properties result = ConfigurationSerializer.deserialize(file);
        assertEquals("localhost", result.getProperty("host"));
        assertEquals("5432", result.getProperty("port"));
        assertEquals("test", result.getProperty("user"));
        assertEquals(3, result.size());
    }

    @Test
    void testDeserialize_mixedCaseKeys_returnsLowercaseProperties() throws IOException, MalformedConfigurationException {
        Path file = tempDir.resolve("config.cfg");

        Files.writeString(file, """
                HoSt=localhost
                PoRt=5432
                UsEr=test
                """);

        Properties result = ConfigurationSerializer.deserialize(file);

        assertEquals("localhost", result.getProperty("host"));
        assertEquals("5432", result.getProperty("port"));
        assertEquals("test", result.getProperty("user"));
        assertEquals(3, result.size());
    }

    @Test
    void testDeserialize_emptyFile_returnsEmptyProperties() throws IOException, MalformedConfigurationException {
        Path file = tempDir.resolve("empty.cfg");
        Files.createFile(file);

        Properties result = ConfigurationSerializer.deserialize(file);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeserialize_malformedUnicodeEscape_throwsMalformedConfigurationException() throws IOException {
        Path file = tempDir.resolve("malformed.cfg");
        Files.writeString(file, "HOST=\\uZZZZ\n");
        assertThrows(MalformedConfigurationException.class, () -> ConfigurationSerializer.deserialize(file));
    }

    @Test
    void testDeserialize_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ConfigurationSerializer.deserialize(null));
    }

    @Test
    void testSerializeDeserialize_validConfiguration_preservesProperties() throws IOException, MalformedConfigurationException {
        Path file = tempDir.resolve("config.cfg");

        Properties original = new Properties();
        original.setProperty("host", "localhost");
        original.setProperty("port", "5432");
        original.setProperty("user", "test");
        original.setProperty("password", "secret");

        ConnectionConfiguration config = mock(ConnectionConfiguration.class);
        when(config.toProperties()).thenReturn(original);

        ConfigurationSerializer.serialize(file, config);
        Properties restored = ConfigurationSerializer.deserialize(file);
        assertEquals(original, restored);
    }
}

