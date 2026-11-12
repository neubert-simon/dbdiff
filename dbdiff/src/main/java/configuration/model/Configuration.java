package configuration.model;

import java.io.Serializable;
import java.util.Properties;

public interface Configuration extends Serializable {

    /**
     * Serializes the connection into {@link Properties}.
     *
     * @return The connection information represented as {@link Properties}
     */
    Properties toProperties();

}
