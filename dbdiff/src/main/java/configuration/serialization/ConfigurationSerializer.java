package configuration.serialization;

import configuration.model.Configuration;
import db.exceptions.MalformedConfigurationException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import utility.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static utility.ArgumentChecker.*;
import static utility.StringUtils.transformPropertyKeys;

@UtilityClass
public final class ConfigurationSerializer {

    public static void serialize(@NonNull Path path, @NonNull Configuration config) throws IOException {
        try (OutputStream out = Files.newOutputStream(path)) {
            Properties properties = transformPropertyKeys(config.toProperties(), String::toUpperCase);
            properties.store(out, "Configuration file");
        }
    }

    public static Properties deserialize(@NonNull Path path) throws IOException, MalformedConfigurationException {
        requireLeafExists(path);
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
            return StringUtils.transformPropertyKeys(properties, String::toLowerCase);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Configuration file %s not found".formatted(path));
        } catch (IOException e) {
            throw new IOException("Configuration file %s could not be read".formatted(path));
        } catch (Exception e) {
            throw new MalformedConfigurationException("""
                    Configuration file not formatted correctly:
                    Please ensure the file only contains lines of this format:
                    ATTRIBUTE=value
                    """ + e.getMessage());
        }
    }

}
