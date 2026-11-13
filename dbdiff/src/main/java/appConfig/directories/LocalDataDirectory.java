package appConfig.directories;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import utility.SystemUtils;

import java.nio.file.Path;

import static appConfig.directories.LocalDataDirectoryManager.APP_NAME;

/**
 * This enumeration lists all directories this application saves local app data to.
 */
@RequiredArgsConstructor
public enum LocalDataDirectory {
    CONFIGURATION(".cfg", true),
    LOGS(".logs", true),
    CONNECTIONS("connections", false),
    SNAPSHOTS("snapshots", false),
    DIFFS("diffs", false),
    ;

    @NotBlank
    public final String name;
    public final boolean hidden;

    public Path getDirectory() {
        return SystemUtils.getLocalAppDirectory().resolve(APP_NAME, this.name);
    }

}
