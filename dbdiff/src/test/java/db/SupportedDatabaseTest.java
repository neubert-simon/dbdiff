package db;

import db.exceptions.UnsupportedDatabaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SupportedDatabaseTest {

    //region getFromName
    @ParameterizedTest
    @EnumSource(SupportedDatabase.class)
    void testGetFromName_withEnumName_returnsExpectedEnumValue(SupportedDatabase database) throws UnsupportedDatabaseException {
        assertEquals(database, SupportedDatabase.getFromName(database.name()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test1", "postkreskl","mysqueal", "sqlight", "null"})
    void testGetFromName_withInvalidName_throwsUnsupportedDatabaseException(String databaseName) {
        assertThrows(UnsupportedDatabaseException.class, () -> SupportedDatabase.getFromName(databaseName));
    }

    @Test
    void testGetFromName_withoutName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SupportedDatabase.getFromName(""));
        assertThrows(IllegalArgumentException.class, () -> SupportedDatabase.getFromName(null));
    }
    //endregion

    //region getSupportedDatabases
    @ParameterizedTest
    @EnumSource(SupportedDatabase.class)
    void testGetSupportedDatabases_withValidName_returnsExpectedEnumValue(SupportedDatabase database) throws UnsupportedDatabaseException {
        assertEquals(database, SupportedDatabase.getFromProductName(database.connectionStringPrefix));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test1", "postkreskl","mysqueal", "sqlight", "null"})
    void testGetSupportedDatabases_withInvalidName_returnsExpectedEnumValue(String databaseName) {
        assertThrows(UnsupportedDatabaseException.class, () -> SupportedDatabase.getFromProductName(databaseName));
    }

    @Test
    void testGetSupportedDatabases_withoutName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SupportedDatabase.getFromProductName(""));
        assertThrows(IllegalArgumentException.class, () -> SupportedDatabase.getFromProductName(null));
    }
    //endregion

}
