package com.github.finley243.adventureengine.gamedata;

public enum ConfigOption {
    INTERFACE_TYPE("interfaceType"),
    ENABLE_DEBUG_LOG("enableDebugLog"),
    PLAYER_STATS("playerStats"),
    PLAYER_ID("playerId"),
    PLAYER_START_AREA("playerStartArea"),
    GAME_NAME("gameName"),
    DEFAULT_DAMAGE_TYPE("defaultDamageType"),
    DEFAULT_LINK_TYPE("defaultLinkType"),
    START_TIME_HOURS("startTimeHours"),
    START_TIME_MINUTES("startTimeMinutes"),
    START_DATE_YEAR("startDateYear"),
    START_DATE_MONTH("startDateMonth"),
    START_DATE_DAY("startDateDay"),
    START_DATE_WEEKDAY("startDateWeekday");

    public final String dataName;

    ConfigOption(String dataName) {
        this.dataName = dataName;
    }
}
