package app;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static app.Main.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    void testExecute_withHelpOption_returnsSuccessExitCode() {
        int exitCode = execute(new String[]{"--help"});
        assertEquals(0, exitCode);
    }

    @Test
    void testExecute_withUnknownOption_returnsUsageErrorExitCode() {
        int exitCode = execute(new String[]{"--nonexistent-option"});
        assertEquals(CommandLine.ExitCode.USAGE, exitCode);
    }

    // TODO performance testing

}
