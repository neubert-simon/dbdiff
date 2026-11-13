package appConfig.directories;

import org.junit.jupiter.api.io.TempDir;
import utility.SystemUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static appConfig.directories.LocalDataDirectoryManager.APP_NAME;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LocalDataDirectoryManagerTest {

    private MockedStatic<SystemUtils> systemUtilsMock;
    private Path tempDir, appDir;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        this.tempDir = tempDir;
        this.appDir = tempDir.resolve(APP_NAME);
        systemUtilsMock = Mockito.mockStatic(SystemUtils.class);
        systemUtilsMock.when(SystemUtils::getLocalAppDirectory).thenReturn(tempDir);
    }

    @AfterEach
    void tearDown() {
        systemUtilsMock.close();
    }

    //region createDirectories
    @Test
    void testCreateDirectories_withEmptyLocalDirectory_createsDirectories() throws IOException {
        LocalDataDirectoryManager.createDirectories();
        for (LocalDataDirectory dir : LocalDataDirectory.values()) {
            assertTrue(Files.isDirectory(tempDir.resolve(APP_NAME, dir.name)));
        }
    }

    @Test
    void testCreateDirectories_withAllDirectoriesPresent_preservesExistingContents()
            throws IOException {

        for (LocalDataDirectory dir : LocalDataDirectory.values()) {
            Path directory = Files.createDirectories(appDir.resolve(dir.name));

            Files.writeString(directory.resolve("existing.txt"), "Test content");
        }

        LocalDataDirectoryManager.createDirectories();

        for (LocalDataDirectory dir : LocalDataDirectory.values()) {
            Path directory = appDir.resolve(dir.name);

            assertTrue(Files.isDirectory(directory));
            assertEquals(
                    "Test content",
                    Files.readString(directory.resolve("existing.txt"))
            );
        }
    }

    @Test
    void testCreateDirectories_withDirectoriesPartiallyPresent_createsMissingDirectories() throws IOException {
        for (LocalDataDirectory dir : List.of(LocalDataDirectory.SNAPSHOTS, LocalDataDirectory.LOGS)) {
            Files.createDirectories(appDir.resolve(dir.name));
            assertTrue(Files.isDirectory(appDir.resolve(dir.name)));
        }
        LocalDataDirectoryManager.createDirectories();
        for (LocalDataDirectory dir : Arrays.stream(LocalDataDirectory.values()).toList()) {
            assertTrue(Files.isDirectory(appDir.resolve(dir.name)));
        }
    }

    @Test
    void testCreateDirectories_withFileSystemUnavailable_throwsIOException() {
        systemUtilsMock.when(SystemUtils::getLocalAppDirectory).thenThrow(new IllegalStateException());
        assertThrows(IllegalStateException.class, LocalDataDirectoryManager::createDirectories);
    }
    //endregion


}
