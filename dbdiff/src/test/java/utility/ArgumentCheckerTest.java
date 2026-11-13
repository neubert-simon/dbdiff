
package utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;
import snapshots.serialization.exceptions.MalformedSnapshotException;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentCheckerTest {

    @TempDir
    Path tempDir;

    //region requireTwoSnapshots
    @Test
    void testRequireTwoSnapshots_withTwoExistingFiles_doesNotThrow()
            throws Exception {
        Path first = Files.createFile(tempDir.resolve("first.snapshot"));
        Path second = Files.createFile(tempDir.resolve("second.snapshot"));

        assertDoesNotThrow(() ->
                ArgumentChecker.requireTwoSnapshots(
                        commandSpec(), new Path[]{first, second}
                )
        );
    }

    @Test
    void testRequireTwoSnapshots_withNoSnapshots_throwsParameterException() {
        assertThrows(CommandLine.ParameterException.class, () ->
                ArgumentChecker.requireTwoSnapshots(
                        commandSpec(), new Path[]{}
                )
        );
    }

    @Test
    void testRequireTwoSnapshots_withOneSnapshot_throwsParameterException()
            throws Exception {
        Path first = Files.createFile(tempDir.resolve("first.snapshot"));

        assertThrows(CommandLine.ParameterException.class, () ->
                ArgumentChecker.requireTwoSnapshots(
                        commandSpec(), new Path[]{first}
                )
        );
    }

    @Test
    void testRequireTwoSnapshots_withThreeSnapshots_throwsParameterException() {
        Path[] snapshots = {
                tempDir.resolve("first.snapshot"),
                tempDir.resolve("second.snapshot"),
                tempDir.resolve("third.snapshot")
        };

        assertThrows(CommandLine.ParameterException.class, () ->
                ArgumentChecker.requireTwoSnapshots(commandSpec(), snapshots)
        );
    }

    @Test
    void testRequireTwoSnapshots_withMissingFile_throwsFileNotFoundException()
            throws Exception {
        Path first = Files.createFile(tempDir.resolve("first.snapshot"));
        Path missing = tempDir.resolve("missing.snapshot");

        assertThrows(FileNotFoundException.class, () ->
                ArgumentChecker.requireTwoSnapshots(
                        commandSpec(), new Path[]{first, missing}
                )
        );
    }

    @Test
    void testRequireTwoSnapshots_withNullSnapshots_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireTwoSnapshots(commandSpec(), null)
        );
    }

    @Test
    void testRequireTwoSnapshots_withNullCommandSpec_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireTwoSnapshots(null, new Path[]{})
        );
    }
    //endregion

    //region getAbsoluteFilepath

    @Test
    void testGetAbsoluteFilepath_withAbsolutePath_returnsUnchangedPath() {
        Path base = tempDir.resolve("config.cfg");
        Path absolute = tempDir.resolve("other.cfg");

        assertEquals(
                absolute,
                SystemUtils.relativeToAbsoluteFilepath(base, absolute)
        );
    }

    @Test
    void testGetAbsoluteFilepath_withRelativePath_returnsResolvedPath() {
        Path base = tempDir.resolve("config.cfg");
        Path relative = Path.of("drivers", "postgresql.jar");

        assertEquals(
                tempDir.resolve(relative),
                SystemUtils.relativeToAbsoluteFilepath(base, relative)
        );
    }

    @Test
    void testGetAbsoluteFilepath_withParentTraversal_returnsNormalizedPath() {
        Path base = tempDir.resolve("configs").resolve("config.cfg");
        Path relative = Path.of("..", "drivers", "postgresql.jar");

        assertEquals(
                tempDir.resolve("drivers").resolve("postgresql.jar"),
                SystemUtils.relativeToAbsoluteFilepath(base, relative)
        );
    }

    @Test
    void testGetAbsoluteFilepath_withNullRelativePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                SystemUtils.relativeToAbsoluteFilepath(tempDir, null)
        );
    }

    @Test
    void testGetAbsoluteFilepath_withNullBaseAndRelativePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                SystemUtils.relativeToAbsoluteFilepath(null, Path.of("file.cfg"))
        );
    }
    //endregion

    //region isBlank
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n", "   \t   "})
    void testIsBlank_withNullEmptyOrWhitespace_returnsTrue(String value) {
        assertTrue(ArgumentChecker.isBlank(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", " hello ", "0", "null", "a\nb"})
    void testIsBlank_withNonBlankString_returnsFalse(String value) {
        assertFalse(ArgumentChecker.isBlank(value));
    }
    //endregion

    //region requireNonBlank
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void testRequireNonBlank_withNullEmptyOrWhitespace_throwsIllegalArgumentException(
            String value
    ) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentChecker.requireNonBlank(value, "username")
        );

        assertEquals("username is required.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", " hello ", "0"})
    void testRequireNonBlank_withNonBlankString_doesNotThrow(String value) {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireNonBlank(value, "username")
        );
    }//endregion

    //region hasNullElements (array)
    @Test
    void testHasNullElements_withNullArray_returnsFalse() {
        assertFalse(ArgumentChecker.hasNullElements((Object[]) null));
    }

    @Test
    void testHasNullElements_withEmptyArray_returnsFalse() {
        assertFalse(ArgumentChecker.hasNullElements(new Object[]{}));
    }

    @Test
    void testHasNullElements_withNullElement_returnsTrue() {
        assertTrue(ArgumentChecker.hasNullElements(
                new Object[]{"first", null, "third"}
        ));
    }

    @Test
    void testHasNullElements_withNoNullElements_returnsFalse() {
        assertFalse(ArgumentChecker.hasNullElements(
                new Object[]{"first", "second", "third"}
        ));
    }

    //endregion

    //region requireNoNullElements (array)
    @Test
    void testRequireNoNullElements_withNullArray_doesNotThrow() {
        assertDoesNotThrow(() -> ArgumentChecker.requireNoNullElements((Object[]) null));
    }

    @Test
    void testRequireNoNullElements_withEmptyArray_doesNotThrow() {
        assertDoesNotThrow(() -> ArgumentChecker.requireNoNullElements(new Object[]{}));
    }

    @Test
    void testRequireNoNullElements_withNullElement_throwsIllegalArgumentException() {
        assertThrows(IllegalStateException.class, () ->
                ArgumentChecker.requireNoNullElements(
                        new Object[]{"first", null}
                )
        );
    }

    @Test
    void testRequireNoNullElements_withNoNullElements_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireNoNullElements(
                        new Object[]{"first", "second"}
                )
        );
    }

    //endregion

    //region hasNullElements (collection)
    @Test
    void testHasNullElements_withNullCollection_returnsTrue() {
        assertTrue(ArgumentChecker.hasNullElements((List<?>) null));
    }

    @Test
    void testHasNullElements_withEmptyCollection_returnsTrue() {
        assertTrue(ArgumentChecker.hasNullElements(List.of()));
    }

    @Test
    void testHasNullElements_withCollectionContainingNull_returnsTrue() {
        assertTrue(ArgumentChecker.hasNullElements(
                Arrays.asList("first", null, "third")
        ));
    }

    @Test
    void testHasNullElements_withCollectionWithoutNull_returnsFalse() {
        assertFalse(ArgumentChecker.hasNullElements(
                List.of("first", "second", "third")
        ));
    }
    //endregion

    //region requireNoNullElements (collection)
    @Test
    void testRequireNoNullElements_withNullCollection_throwsIllegalArgumentException() {
        assertThrows(IllegalStateException.class, () ->
                ArgumentChecker.requireNoNullElements((List<?>) null)
        );
    }

    @Test
    void testRequireNoNullElements_withEmptyCollection_throwsIllegalArgumentException() {
        assertThrows(IllegalStateException.class, () ->
                ArgumentChecker.requireNoNullElements(List.of())
        );
    }

    @Test
    void testRequireNoNullElements_withCollectionContainingNull_throwsIllegalArgumentException() {
        assertThrows(IllegalStateException.class, () ->
                ArgumentChecker.requireNoNullElements(
                        Arrays.asList("first", null)
                )
        );
    }

    @Test
    void testRequireNoNullElements_withCollectionWithoutNull_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireNoNullElements(
                        List.of("first", "second")
                )
        );
    }
    //endregion

    //region requireFileExists
    @Test
    void testRequireLeafExists_withExistingFile_doesNotThrow()
            throws Exception {
        Path file = Files.createFile(tempDir.resolve("existing.txt"));

        assertDoesNotThrow(() ->
                ArgumentChecker.requireLeafExists(file)
        );
    }

    @Test
    void testRequireLeafExists_withMissingFile_throwsFileNotFoundException() {
        Path missing = tempDir.resolve("missing.txt");

        assertThrows(FileNotFoundException.class, () ->
                ArgumentChecker.requireLeafExists(missing)
        );
    }

    @Test
    void testRequireLeafExists_withExistingDirectory_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireLeafExists(tempDir)
        );
    }

    @Test
    void testRequireLeafExists_withNullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireLeafExists((Path) null)
        );
    }

    //endregion

    //region requireFileExists (array)

    @Test
    void testRequireLeafExists_withAllExistingFiles_doesNotThrow()
            throws Exception {
        Path first = Files.createFile(tempDir.resolve("first.txt"));
        Path second = Files.createFile(tempDir.resolve("second.txt"));

        assertDoesNotThrow(() ->
                ArgumentChecker.requireLeafExists(new Path[]{first, second})
        );
    }

    @Test
    void testRequireLeafExists_withOneMissingFile_throwsFileNotFoundException()
            throws Exception {
        Path first = Files.createFile(tempDir.resolve("first.txt"));
        Path missing = tempDir.resolve("missing.txt");

        assertThrows(FileNotFoundException.class, () ->
                ArgumentChecker.requireLeafExists(new Path[]{first, missing})
        );
    }

    @Test
    void testRequireLeafExists_withEmptyArray_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireLeafExists(new Path[]{})
        );
    }

    @Test
    void testRequireLeafExists_withNullArray_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireLeafExists((Path[]) null)
        );
    }

    @Test
    void testRequireLeafExists_withNullArrayElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireLeafExists(new Path[]{null})
        );
    }
    //endregion

    //region requireFileExtension
    @Test
    void testRequireLeafExtension_withMatchingExtension_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireFileExtension(
                        Path.of("snapshot.json"), ".json"
                )
        );
    }

    @Test
    void testRequireLeafExtension_withDifferentExtension_throwsInvalidFileException() {
        assertThrows(MalformedSnapshotException.class, () ->
                ArgumentChecker.requireFileExtension(
                        Path.of("snapshot.txt"), ".json"
                )
        );
    }

    @Test
    void testRequireLeafExtension_withMissingExtension_throwsInvalidFileException() {
        assertThrows(MalformedSnapshotException.class, () ->
                ArgumentChecker.requireFileExtension(
                        Path.of("snapshot"), ".json"
                )
        );
    }

    @Test
    void testRequireLeafExtension_withUppercaseExtension_throwsInvalidFileException() {
        assertThrows(MalformedSnapshotException.class, () ->
                ArgumentChecker.requireFileExtension(
                        Path.of("snapshot.JSON"), ".json"
                )
        );
    }

    @Test
    void testRequireLeafExtension_withMatchingSuffix_doesNotThrow() {
        assertDoesNotThrow(() ->
                ArgumentChecker.requireFileExtension(
                        Path.of("snapshotjson"), "json"
                )
        );
    }

    @Test
    void testRequireLeafExtension_withNullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                ArgumentChecker.requireFileExtension(null, ".json")
        );
    }
    //endregion

    //region Auxiliary
    private CommandLine.Model.CommandSpec commandSpec() {
        return new CommandLine(new DummyCommand()).getCommandSpec();
    }

    @CommandLine.Command(name = "test")
    private static class DummyCommand implements Runnable {
        @Override
        public void run() {
        }
    }
    //endregion
}
