package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.actor.Faction;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionLoader {

    private static final String NAME_FACTION = "faction";
    private static final String NAME_ID = "id";
    private static final String NAME_DEFAULT_RELATION = "default";

    private static final String NAME_RELATION = "relation";
    private static final String NAME_RELATION_FACTION = "faction";
    private static final String NAME_RELATION_TYPE = "type";

    private static final Faction.FactionRelation DEFAULT_DEFAULT_RELATION = Faction.FactionRelation.NEUTRAL;
    private static final Faction.FactionRelation DEFAULT_RELATION_TYPE = Faction.FactionRelation.NEUTRAL;

    public Map<String, Faction> load(Element element) {
        return LoadUtils.loadAll(element, NAME_FACTION, this::parseFaction, Faction::getID);
    }

    private Faction parseFaction(Element element) {
        if (element == null) return null;
        String id = LoadUtils.attribute(element, NAME_ID, null);
        Faction.FactionRelation defaultRelation;
        try {
            defaultRelation = LoadUtils.attributeEnum(element, NAME_DEFAULT_RELATION, Faction.FactionRelation.class, DEFAULT_DEFAULT_RELATION);
        } catch (IllegalArgumentException e) {
            throw new GameDataException("Faction has invalid default relation type");
        }
        Map<String, Faction.FactionRelation> relations = parseFactionRelations(element);
        return new Faction(id, defaultRelation, relations);
    }

    private Map<String, Faction.FactionRelation> parseFactionRelations(Element element) {
        if (element == null) return new HashMap<>();
        Map<String, Faction.FactionRelation> relations = new HashMap<>();
        List<Element> relationElements = LoadUtils.directChildrenWithName(element, NAME_RELATION);
        for (Element relationElement : relationElements) {
            String factionID = LoadUtils.attribute(relationElement, NAME_RELATION_FACTION, null);
            Faction.FactionRelation type;
            try {
                type = LoadUtils.attributeEnum(relationElement, NAME_RELATION_TYPE, Faction.FactionRelation.class, DEFAULT_RELATION_TYPE);
            } catch (IllegalArgumentException e) {
                throw new GameDataException("FactionRelation has invalid relation type");
            }
            relations.put(factionID, type);
        }
        return relations;
    }

}
