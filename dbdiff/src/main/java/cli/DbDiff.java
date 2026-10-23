package cli;

import db.connection.DataSourceDispatcher;
import lombok.NonNull;
import picocli.CommandLine.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.concurrent.Callable;

@Command(name = "dbdiff", mixinStandardHelpOptions = true, version = "1.0", description = "Database diffing tool")
public class DbDiff implements Callable<Integer> {

    @Spec
    private Model.CommandSpec spec;

    //region Database connection
    @Option(names = {"-u", "--url"}, description = "The database connection URL")
    private String url;

    @Option(names = {"-c", "--config"}, description = "Filepath to the database connection config file")
    private File config;
    //endregion

    //region Database interfacing
    @Option(names = {"-t", "--table"}, description = "The name of the table")
    private String table;
    //endregion

    //region IO operations
    @Option(names = {"-s", "--snapshot"}, description = "Create a snapshot")
    private boolean shouldCreateSnapshot;

    @Option(names = {"-o", "--output"}, description = "Filepath to save a snapshot to")
    private String output;
    //endregion

    //region Diffing
    @Parameters(index = "0", arity = "0..2", description = "Paths to the two snapshots to diff")
    private File[] snapshots;
    //endregion

    //region Miscellaneous
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help and exit")
    private boolean help;

    @Option(names = {"-l", "--list"}, description = "List all snapshots")
    private boolean shouldList;
    //endregion

    @Override
    public Integer call() throws FileNotFoundException {

        // Mode 1: Diffing
        if (snapshots != null) {
            requireTwoSnapshots(snapshots);
            // TODO Implement diffing
            return 0;
        }

        // Mode 2: Configuring the application
        // TODO Implement connecting to db via config file
        try (Connection conn = DataSourceDispatcher.connectDataSource(url)) {
            String s = conn.toString();
            IO.println(s);
        } catch (Exception e) {
            IO.println(e.getMessage());
        }

        // Mode 3: Creating snapshots

        return 0;
    }

    private void requireTwoSnapshots(@NonNull File[] snapshots) throws FileNotFoundException {
        if (snapshots.length != 2) {
            throw new ParameterException(spec.commandLine(), "Positional parameters must be 2 database snapshots");
        }
        for (File snapshot : snapshots) {
            if (!snapshot.exists()) {
                throw new FileNotFoundException("Snapshot \"" + snapshot.getPath() + "\" not found");
            }
        }
        // TODO implement file extension and syntax check
    }
}