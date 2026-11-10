package utility;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import picocli.CommandLine;
import utility.exceptions.FileFormatException;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collection;

@UtilityClass
public final class ArgumentChecker {

    private static final String NULL_MSG = "Value cannot be null.";
    private static final String REQUIRED_MSG = " is required.";

    //region Domain specific
    public static void requireTwoSnapshots(@NonNull CommandLine.Model.CommandSpec spec, @NonNull Path[] snapshots) throws FileNotFoundException {
        if (snapshots.length != 2) {
            throw new CommandLine.ParameterException(spec.commandLine(), "Positional parameters must be 2 engine snapshots");
        }
        requireLeafExists(snapshots);
    }
    //endregion

    //region String
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static void requireNonBlank(String str, @NotBlank String parameterName) {
        if (isBlank(str)) throw new IllegalArgumentException(parameterName + REQUIRED_MSG);
    }
    //endregion

    //region Arrays
    public static boolean hasNullElements(Object[] arr) {
        if (arr == null) return false;
        for (Object o : arr) {
            if (o == null) return true;
        }
        return false;
    }

    public static void requireNoNullElements(Object[] arr) {
        if (hasNullElements(arr)) throw new IllegalStateException(NULL_MSG);
    }
    //endregion

    //region Lists
    public static boolean hasNullElements(Collection<?> col) {
        if (col == null || col.isEmpty()) return true;
        for (Object o : col) {
            if (o == null) return true;
        }
        return false;
    }

    public static void requireNoNullElements(Collection<?> col) {
        if (hasNullElements(col)) throw new IllegalStateException(NULL_MSG);
    }
    //endregion

    //region Files
    public static void requireLeafExists(Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("File or directory \"" + path.toAbsolutePath() + "\" not found.");
        }
    }

    public static void requireLeafExists(Path[] filePaths) throws FileNotFoundException {
        for (Path path : filePaths) {
            requireLeafExists(path);
        }
    }

    public static void requireFileExtension(Path path, String fileExtension) throws FileFormatException {
        if (!path.toString().endsWith(fileExtension)) {
            throw new FileFormatException(path, fileExtension);
        }
    }
    //endregion

}
