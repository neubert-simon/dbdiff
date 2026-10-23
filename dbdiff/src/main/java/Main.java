import cli.DbDiff;
import picocli.CommandLine;

public class Main {

    // TODO --enable-native-access=ALL-UNNAMED when deploying

    static void main(String[] args) {
        CommandLine cmd = new CommandLine(new DbDiff()).setExecutionExceptionHandler(
                (ex, commandLine, _) -> {
                    commandLine.getErr().println(ex.getMessage());
                    return 1;
                });
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

}
