package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.gamedata.GameData;

import java.io.File;

public class GameDataLoader {

    private static final String FILE_EXTENSION = ".xml";

    private static final String NAME_FACTION = "factions";
    private static final String NAME_DAMAGE_TYPE = "damage_types";
    private static final String NAME_ATTACK_TYPE = "attack_types";
    private static final String NAME_ATTRIBUTE = "attributes";
    private static final String NAME_SKILL = "skills";
    private static final String NAME_SENSE_TYPE = "sense_types";
    private static final String NAME_OBSTRUCTION_TYPE = "obstruction_types";
    private static final String NAME_LINK_TYPE = "link_types";
    private static final String NAME_WEAPON_CLASS = "weapon_classes";

    private static final String NAME_EFFECT = "effects";
    private static final String NAME_ACTION_TEMPLATE = "actions";
    private static final String NAME_OBJECT_TEMPLATE = "objects";
    private static final String NAME_ITEM_TEMPLATE = "items";
    private static final String NAME_LOOT_TABLE = "loot_tables";
    private static final String NAME_ACTOR_TEMPLATE = "actors";
    private static final String NAME_NETWORK_NODE = "networks";
    private static final String NAME_SCENE = "scenes";
    private static final String NAME_ROOM = "rooms";
    private static final String NAME_AREA = "areas";


    public GameData loadData(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("File must be a directory: " + dir.getAbsolutePath());
        }
        File factionFile = new File(dir, NAME_FACTION + FILE_EXTENSION);
        if (!factionFile.exists()) throw new IllegalArgumentException("File does not exist: " + factionFile.getAbsolutePath());

    }

}
