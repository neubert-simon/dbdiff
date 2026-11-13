package appConfig.directories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utility.SystemUtils;

import java.nio.file.Path;

import static appConfig.directories.LocalDataDirectoryManager.APP_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static testUtility.fixtures.utility.OperatingSystemTestFixtures.HOME;

public class LocalDataDirectoryTest {

    private MockedStatic<SystemUtils> systemUtilsMock;

    @BeforeEach
    void setUp() {
        systemUtilsMock = Mockito.mockStatic(SystemUtils.class);
        mockSystemUtils();
    }

    @AfterEach
    void tearDown() {
        systemUtilsMock.close();
    }

    //region getDirectory
    @ParameterizedTest
    @EnumSource(LocalDataDirectory.class)
    public void testGetDirectory_withLocalAppDirectory_returnsExpectedDirectory(LocalDataDirectory localDataDirectory) {
        assertEquals(Path.of(HOME, APP_NAME, localDataDirectory.name), localDataDirectory.getDirectory());
    }
    //endregion

    //region Auxiliary
    void mockSystemUtils() {
        systemUtilsMock.when(SystemUtils::getLocalAppDirectory).thenReturn(Path.of(HOME));
    }
    //endregion

}
