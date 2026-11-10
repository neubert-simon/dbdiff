package db.driver;

import db.exceptions.DriverUnableToConnectException;
import db.exceptions.SQLExceptionHandler;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

public class DriverLoaderJar implements DriverLoader, AutoCloseable {

    private final Path jar;
    private final String targetUrl;
    private final URLClassLoader classLoader;

    /**
     * Constructs a {@link DriverLoader} for Jar drivers.
     *
     * @param jar            The filepath to the jar file.
     * @param targetUrl The url to check the driver options against. This is necessary because a jar can contain multiple driver instances.
     */
    public DriverLoaderJar(@NonNull Path jar, @NonNull String targetUrl) throws MalformedURLException {
        this.jar = jar;
        this.targetUrl = targetUrl;
        this.classLoader = new URLClassLoader(
                new URL[]{jar.toUri().toURL()},
                Driver.class.getClassLoader()
        );
    }

    @Override
    public Driver load() throws DriverUnableToConnectException, SQLException {
        try {
            return loadDriverFromJar();
        } catch (MalformedURLException e) {
            throw new DriverUnableToConnectException("Malformed connection URL: " + targetUrl);
        } catch (SQLException e) {
            throw SQLExceptionHandler.createActionableSQLException(e, "connection establishment");
        } catch (IOException e) {
            throw new DriverUnableToConnectException("Error during driver selection by the user.");
        }
    }

    @Override
    public void close() throws IOException {
        classLoader.close();
    }

    /**
     * Gets a driver from the provided JAR file.
     * If multiple drivers from the archive accept {@link targetUrl}, the user is prompted to
     * choose the appropriate driver.
     *
     * @return The {@link Driver} which connect to the target.
     * @throws MalformedURLException          If the JAR path can't be converted to a valid URL.
     * @throws SQLException                   If a database error occurs when checking the driver's capability to connect.
     * @throws DriverUnableToConnectException If no suitable driver is found.
     */
    private Driver loadDriverFromJar() throws IOException, SQLException, DriverUnableToConnectException {
        Iterable<Driver> drivers = ServiceLoader.load(Driver.class, classLoader);
        Set<Driver> suitableDrivers = new HashSet<>();

        for (Driver driver : drivers) {
            if (driver.acceptsURL(targetUrl)) {
                suitableDrivers.add(driver);
            }
        }

        if (suitableDrivers.size() == 1) return suitableDrivers.iterator().next();

        if (suitableDrivers.isEmpty()) throw new DriverUnableToConnectException(
                "No suitable JDBC driver found for: " + targetUrl + " in the provided JAR archive: " + jar
        );

        return getDriverFromUserInput(suitableDrivers);
    }

    private Driver getDriverFromUserInput(Set<Driver> suitableDrivers) throws IOException {
        Driver[] drivers = suitableDrivers.toArray(Driver[]::new);

        System.out.println("Multiple drivers found.\nPlease choose the appropriate class to load:");

        int driverIndex = -1;

        for (int i = 0; i < drivers.length; i++) {
            System.out.printf("%d\t%s%n", i + 1, drivers[i].getClass().getName());
        }

        while (driverIndex == -1) {
            System.out.printf("Select a driver (1-%d): ", drivers.length);
            String input =  new BufferedReader(new InputStreamReader(System.in)).readLine();

            if (input == null) throw new EOFException("No driver selected.");

            try {
                int index = Integer.parseInt(input.trim()) - 1;
                if (index >= 0 && index < drivers.length) driverIndex = index;
            } catch (NumberFormatException _) {
                System.out.println("Invalid selection. Please enter a number between 1 and " + drivers.length + " and try again.");
            }
        }

        return drivers[driverIndex];
    }
}
