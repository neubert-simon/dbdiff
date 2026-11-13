package utility;

import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.util.DefaultLocale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static utility.StringUtils.*;

class StringUtilsTest {

    private final String TEST_NAME = "testName";
    private final Instant TEST_INSTANT = Instant.ofEpochMilli(450_000_000_000L); // 05.04.1984, 10:00:00

    private MockedStatic<StringUtils> stringUtilsMock;

    @BeforeEach
    void setUp() {
        stringUtilsMock = Mockito.mockStatic(StringUtils.class, Mockito.CALLS_REAL_METHODS);
    }

    @AfterEach
    void tearDown() {
        stringUtilsMock.close();
    }

    //region generateSnapshotName
    @Test
    void testGenerateSnapshotName_withTableAndInstant_returnsExpectedString() {
        final String TEST_TIME = "dateTime";
        stringUtilsMock.when(() -> StringUtils.instantToDateTimeString(any())).thenReturn(TEST_TIME);
        final String SEPARATOR = " - ";
        assertEquals(TEST_NAME + SEPARATOR + TEST_TIME, generateSnapshotName("testName"));
    }

    @Test
    void testGenerateSnapshotName_withoutTable_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> generateSnapshotName(null));
    }
    //endregion generateSnapshotName

    //region instantToDateTimeString
    @Test
    @DefaultLocale("de-DE")
    void testInstantToDateTimeString_withInstant_returnsExpectedString() {
        assertEquals("05.04.1984 10:00:00", instantToDateTimeString(TEST_INSTANT));
    }

    @Test
    void testInstantToDateTimeString_withoutInstant_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> instantToDateTimeString(null));
    }
    //endregion

    //region transformPropertyKeys
    // region transformPropertyKeys

    @ParameterizedTest
    @MethodSource("keyTransformations")
    void testTransformPropertyKeys_withValidTransformation_returnsTransformedKeys(
            UnaryOperator<String> transformation,
            String expectedKey
    ) {
        Properties properties = new Properties();
        properties.setProperty("Database", "POSTGRESQL");

        Properties result = transformPropertyKeys(
                properties, transformation
        );

        assertEquals(1, result.size());
        assertEquals("POSTGRESQL", result.getProperty(expectedKey));
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> keyTransformations() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        (UnaryOperator<String>) String::toUpperCase,
                        "DATABASE"
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        (UnaryOperator<String>) String::toLowerCase,
                        "database"
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        (UnaryOperator<String>) key -> "PREFIX_" + key,
                        "PREFIX_Database"
                )
        );
    }

    @Test
    void testTransformPropertyKeys_withEmptyProperties_returnsEmptyProperties() {
        Properties properties = new Properties();

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void testTransformPropertyKeys_withMultipleProperties_transformsAllKeys() {
        Properties properties = new Properties();
        properties.setProperty("host", "localhost");
        properties.setProperty("port", "5432");
        properties.setProperty("database", "POSTGRESQL");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(3, result.size());
        assertEquals("localhost", result.getProperty("HOST"));
        assertEquals("5432", result.getProperty("PORT"));
        assertEquals("POSTGRESQL", result.getProperty("DATABASE"));
    }

    @Test
    void testTransformPropertyKeys_withWhitespaceAroundKeys_stripsWhitespace() {
        Properties properties = new Properties();
        properties.setProperty("  database \t", "POSTGRESQL");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(1, result.size());
        assertEquals("POSTGRESQL", result.getProperty("DATABASE"));
        assertNull(result.getProperty("  database \t"));
    }

    @Test
    void testTransformPropertyKeys_withWhitespaceInsideKeys_preservesInternalWhitespace() {
        Properties properties = new Properties();
        properties.setProperty("data base", "POSTGRESQL");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals("POSTGRESQL", result.getProperty("DATA BASE"));
    }

    @Test
    void testTransformPropertyKeys_withWhitespaceInValues_preservesValues() {
        Properties properties = new Properties();
        properties.setProperty("host", "  localhost  ");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals("  localhost  ", result.getProperty("HOST"));
    }

    @Test
    void testTransformPropertyKeys_withEmptyValue_preservesEmptyValue() {
        Properties properties = new Properties();
        properties.setProperty("password", "");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals("", result.getProperty("PASSWORD"));
    }

    @Test
    void testTransformPropertyKeys_withEmptyKey_preservesEmptyKey() {
        Properties properties = new Properties();
        properties.setProperty("", "value");

        Properties result = transformPropertyKeys(
                properties, UnaryOperator.identity()
        );

        assertEquals("value", result.getProperty(""));
    }

    @Test
    void testTransformPropertyKeys_withNullProperties_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> transformPropertyKeys(null, String::toUpperCase));
    }

    @Test
    void testTransformPropertyKeys_withNullTransformation_throwsNullPointerException() {
        Properties properties = new Properties();
        properties.setProperty("database", "POSTGRESQL");

        assertThrows(NullPointerException.class, () ->
                transformPropertyKeys(properties, null)
        );
    }

    @Test
    void testTransformPropertyKeys_withTransformationReturningNull_throwsNullPointerException() {
        Properties properties = new Properties();
        properties.setProperty("database", "POSTGRESQL");

        assertThrows(NullPointerException.class, () ->
                transformPropertyKeys(
                        properties, _ -> null
                )
        );
    }

    @Test
    void testTransformPropertyKeys_withTransformationThrowingException_propagatesException() {
        Properties properties = new Properties();
        properties.setProperty("database", "POSTGRESQL");

        IllegalStateException expected = new IllegalStateException("Transformation failed");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> transformPropertyKeys(
                        properties,
                        _ -> {
                            throw expected;
                        }
                )
        );

        assertSame(expected, actual);
    }

    @Test
    void testTransformPropertyKeys_withCollidingKeys_overwritesOneValue() {
        Properties properties = new Properties();
        properties.setProperty("HOST", "first");
        properties.setProperty("host", "second");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(1, result.size());
        assertTrue(
                "first".equals(result.getProperty("HOST"))
                        || "second".equals(result.getProperty("HOST"))
        );
    }

    @Test
    void testTransformPropertyKeys_withNonStringEntry_ignoresNonStringEntry() {
        Properties properties = new Properties();
        properties.setProperty("database", "POSTGRESQL");
        properties.put(123, "ignored");
        properties.put("nonStringValue", 456);

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(1, result.size());
        assertEquals("POSTGRESQL", result.getProperty("DATABASE"));
    }

    @Test
    void testTransformPropertyKeys_withDefaultProperties_includesInheritedProperties() {
        Properties defaults = new Properties();
        defaults.setProperty("host", "localhost");

        Properties properties = new Properties(defaults);
        properties.setProperty("port", "5432");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(2, result.size());
        assertEquals("localhost", result.getProperty("HOST"));
        assertEquals("5432", result.getProperty("PORT"));
    }

    @Test
    void testTransformPropertyKeys_withOverriddenDefault_usesOverridingValue() {
        Properties defaults = new Properties();
        defaults.setProperty("host", "default-host");

        Properties properties = new Properties(defaults);
        properties.setProperty("host", "custom-host");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals(1, result.size());
        assertEquals("custom-host", result.getProperty("HOST"));
    }

    @Test
    void testTransformPropertyKeys_withValidProperties_doesNotModifyOriginal() {
        Properties properties = new Properties();
        properties.setProperty("  database  ", "POSTGRESQL");

        Properties result = transformPropertyKeys(
                properties, String::toUpperCase
        );

        assertEquals("POSTGRESQL", properties.getProperty("  database  "));
        assertNull(properties.getProperty("DATABASE"));
        assertEquals(1, properties.size());

        assertNotSame(properties, result);
        assertEquals("POSTGRESQL", result.getProperty("DATABASE"));
    }

    // endregion
    //endregion

    //region appendFileExtensionIfNotPresent
    @ParameterizedTest
    @ValueSource(strings = {".json", ".txt", ".db", ".cfg", ".sn"})
    void testAppendFileExtensionIfNotPresent_withPathAndExtension_returnsExpectedPath(String extension) {
        final String TEST_PATH = "/" + TEST_NAME;
        assertEquals(Path.of(TEST_PATH + extension), appendFileExtensionIfNotPresent(Path.of(TEST_PATH), extension));
    }

    @Test
    void testAppendFileExtensionIfNotPresent_withAnyNullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> appendFileExtensionIfNotPresent(Path.of("/"), null));
        assertThrows(NullPointerException.class, () -> appendFileExtensionIfNotPresent(null, ""));
        assertThrows(NullPointerException.class, () -> appendFileExtensionIfNotPresent(null, null));
    }
    //endregion
}