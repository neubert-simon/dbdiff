package db.connection.configuration.properties;

import configuration.model.ConfigurationAttribute;

import java.util.Properties;

/**
 * This enum stores attribute names for configuration syntax.
 * Configuration attributes should be set using {@link ConnectionConfigurationAttribute#key()}
 */
public enum ConnectionConfigurationAttribute implements ConfigurationAttribute {
    DATABASE,
    URL,
    FILEPATH,
    HOST,
    PORT,
    NAME,
    USER,
    PASSWORD,
    DRIVER;

    /**
     * Converts the enum name to the name which should bey used as a connection's {@link Properties} key.
     *
     * @return The lowercased property name.
     */
    public String key() {
        return this.name().toLowerCase();
    }
}
