package com.github.finley243.adventureengine.gamedata;

import java.util.Map;

public class ConfigHandler {

    private final Map<ConfigOption, String> configEntries;

    public ConfigHandler(Map<ConfigOption, String> configEntries) {
        this.configEntries = configEntries;
    }

    public String get(ConfigOption configOption) {
        return configEntries.get(configOption);
    }

}
