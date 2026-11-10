package utility.exceptions;

import java.nio.file.Path;

public class FileFormatException extends Exception {
    public FileFormatException(Path path, String fileExtension) {
        super(path.toAbsolutePath() + " is not a %s file."
                .formatted(fileExtension.replace(".", "").toUpperCase()));
    }
}
