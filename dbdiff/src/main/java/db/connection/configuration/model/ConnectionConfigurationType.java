package db.connection.configuration.model;

import db.connection.configuration.properties.ConnectionConfigurationAttribute;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum
ConnectionConfigurationType {

    HOST_CONFIGURATION(HostConfiguration.REQUIRED_ATTRIBUTES),
    URL_CONFIGURATION(UrlConfiguration.REQUIRED_ATTRIBUTES),
    FILE_CONFIGURATION(FileConfiguration.REQUIRED_ATTRIBUTES),
    ;

    public final Set<ConnectionConfigurationAttribute> requiredAttributes;

    //region java.lang.Object Overrides
    @Override
    public String toString() {
        return String.join(
                " ",
                Arrays.stream(this.name().split("_"))
                        .map(String::toLowerCase)
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining(" ")));
    }
    //endregion

}
