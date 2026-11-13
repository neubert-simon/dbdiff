package appConfig.directories;

import utility.SystemUtils;
import cli.CLI;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public final class LocalDataDirectoryManager {

    /**
     * Application name to use with directory creating and OS interfacing.
     */
    public static final String APP_NAME = "dbdiff";

    /**
     * Creates local data directories if they don't already exist.
     * This method should be invoked by {@link CLI#call()} on startup.
     *
     * @throws IOException if directory creation or access fails
     */
    public static void createDirectories() throws IOException {
        Path appDirectory = SystemUtils.getLocalAppDirectory().resolve(APP_NAME);
        for (LocalDataDirectory dir : LocalDataDirectory.values()) {
            Files.createDirectories(appDirectory.resolve(dir.name));
        }
    }

}
