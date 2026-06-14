package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.actor.Attribute;
import com.github.finley243.adventureengine.actor.SenseType;
import com.github.finley243.adventureengine.actor.Skill;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.world.obstruction.ObstructionType;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CharacterTypeLoader {

    private static final String NAME_ATTRIBUTE = "attribute";
    private static final String NAME_SKILL = "skill";
    private static final String NAME_SENSE_TYPE = "senseType";

    private static final String NAME_ATTRIBUTE_ID = "id";

    private static final String NAME_SKILL_ID = "id";

    private static final String NAME_SENSE_TYPE_ID = "id";
    private static final String NAME_SENSE_TYPE_NAME = "name";
    private static final String NAME_SENSE_TYPE_OBSTRUCTIONS = "bypassedObstructionType";

    public Map<String, Attribute> loadAttributes(Element element) {
        return LoadUtils.loadAll(element, NAME_ATTRIBUTE, this::parseAttribute, Attribute::ID);
    }

    public Map<String, Skill> loadSkills(Element element) {
        return LoadUtils.loadAll(element, NAME_SKILL, this::parseSkill, Skill::ID);
    }

    public Map<String, SenseType> loadSenseTypes(Element element, Registry<ObstructionType> obstructionTypeRegistry) {
        return LoadUtils.loadAll(element, NAME_SENSE_TYPE, e -> parseSenseType(e, obstructionTypeRegistry), SenseType::ID);
    }

    private Attribute parseAttribute(Element element) {
        String ID = LoadUtils.attribute(element, NAME_ATTRIBUTE_ID, null);
        String name = element.getTextContent();
        return new Attribute(ID, name);
    }

    private Skill parseSkill(Element element) {
        String ID = LoadUtils.attribute(element, NAME_SKILL_ID, null);
        String name = element.getTextContent();
        return new Skill(ID, name);
    }

    private SenseType parseSenseType(Element element, Registry<ObstructionType> obstructionTypeRegistry) {
        String ID = LoadUtils.attribute(element, NAME_SENSE_TYPE_ID, null);
        String name = LoadUtils.singleTag(element, NAME_SENSE_TYPE_NAME, null);
        Set<String> bypassedObstructionTypeIDs = LoadUtils.setOfTags(element, NAME_SENSE_TYPE_OBSTRUCTIONS);
        Set<ObstructionType> bypassedObstructionTypes = new HashSet<>();
        for (String obstructionTypeID : bypassedObstructionTypeIDs) {
            ObstructionType obstructionType = obstructionTypeRegistry.getFromID(obstructionTypeID);
            if (obstructionType == null) throw new GameDataException("SenseType has invalid bypassed obstruction type");
            bypassedObstructionTypes.add(obstructionType);
        }
        return new SenseType(ID, name, bypassedObstructionTypes);
    }

}
