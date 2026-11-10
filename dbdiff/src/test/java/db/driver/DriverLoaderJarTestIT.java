package db.driver;

import db.SupportedDatabase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Path;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DriverLoaderJarTestIT {

    private final Map<SupportedDatabase, String> connectionUrls = Map.of(
            SupportedDatabase.POSTGRESQL, "jdbc:postgresql://localhost:5432/test",
            SupportedDatabase.MYSQL, "jdbc:mysql://localhost:5432/test",
            SupportedDatabase.SQLITE, "jdbc:sqlite:test.db"
    );

    private final Map<SupportedDatabase, String> classNames = Map.of(
            SupportedDatabase.POSTGRESQL, "org.postgresql.Driver",
            SupportedDatabase.MYSQL, "com.mysql.cj.jdbc.Driver",
            SupportedDatabase.SQLITE, "org.sqlite.JDBC"
    );

    @ParameterizedTest
    @EnumSource(SupportedDatabase.class)
    void testLoad_withValidDriver_loadsDriverDynamically(SupportedDatabase database) throws SQLException {
        Path driverJar = Path.of(System.getProperty("project.build.directory"),
                "test-drivers",
                database.connectionStringPrefix + ".jar");

        DriverLoader loader = assertDoesNotThrow(() -> new DriverLoaderJar(driverJar, connectionUrls.get(database)));
        Driver driver = assertDoesNotThrow(loader::load);

        assertNotNull(driver);
        assertTrue(driver.acceptsURL(connectionUrls.get(database)));
        assertEquals(classNames.get(database), driver.getClass().getName());
        assertDoesNotThrow(loader::close);

    }
}
