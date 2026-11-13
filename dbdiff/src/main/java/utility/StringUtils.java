package utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Properties;
import java.util.function.UnaryOperator;

import static java.time.temporal.ChronoField.*;

@UtilityClass
public class StringUtils {

    //region File name generation
    /**
     * Generates a snapshot name in the following format:<br>
     * {@code table_name - creation_date_time}
     * @param table Name of the table the snapshot was taken from
     * @return The generated name
     */
    public static String generateSnapshotName(@NonNull String table) {
        return table + " - " + instantToDateTimeString(Instant.now());
    }
    //endregion

    //region DateTime
    /**
     * Creates a human-readable date and time {@link String} from an Instant.
     * @param instant The instant to be turned into date and time.
     * @return The formatted {@link String}.
     */
    public static String instantToDateTimeString(@NonNull Instant instant) {
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .toFormatter()
                .withZone(ZoneId.systemDefault());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withZone(ZoneId.systemDefault());
        return dateFormatter.format(instant).replace('-', '.').concat(" ").concat(timeFormatter.format(instant));
    }
    //endregion

    //region Properties
    /**
     * Transforms all {@link String} keys of a {@link Properties} object.
     * @param properties Properties whose keys need to be processed.
     * @param transformation The function to apply.
     * @return The newly created properties with transformed keys.
     */
    public static Properties transformPropertyKeys(@NonNull Properties properties, UnaryOperator<String> transformation) {
        Properties result = new Properties();

        for (String key : properties.stringPropertyNames()) {
            result.setProperty(
                    transformation.apply(key.strip()),
                    properties.getProperty(key)
            );
        }

        return result;
    }
    //endregion

    //region Paths
    public static Path appendFileExtensionIfNotPresent(@NonNull Path path, @NonNull String fileExtension) {
        return path.toString().endsWith(fileExtension) ? path : Path.of(path + fileExtension);
    }
}
