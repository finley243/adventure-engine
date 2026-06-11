package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.LootTableEntry;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootTableLoader {

    private static final String NAME_LOOT_TABLE = "lootTable";

    public LootTableLoader() {

    }

    public Map<String, LootTable> load(Element element) {
        return LoadUtils.loadAll(element, NAME_LOOT_TABLE, e -> this.parseLootTable(e, false), LootTable::getID);
    }

    LootTable parseLootTable(Element element, boolean useAllDefault) {
        if (element == null) return null;
        String tableID = element.getAttribute("id");
        boolean useAll = LoadUtils.attributeBool(element, "useAll", useAllDefault);
        List<Element> entryItems = LoadUtils.directChildrenWithName(element, "item");
        List<Element> entryTables = LoadUtils.directChildrenWithName(element, "table");
        List<LootTableEntry> entries = new ArrayList<>();
        for (Element entryItem : entryItems) {
            LootTableEntry entry = parseLootTableEntry(entryItem, false);
            entries.add(entry);
        }
        for (Element entryTable : entryTables) {
            LootTableEntry entry = parseLootTableEntry(entryTable, true);
            entries.add(entry);
        }
        return new LootTable(tableID, useAll, entries);
    }

    private LootTableEntry parseLootTableEntry(Element element, boolean isTable) {
        if (element == null) return null;
        String referenceID = element.getTextContent();
        float chance = LoadUtils.attributeFloat(element, "chance", 1.0f);
        int count = LoadUtils.attributeInt(element, "count", 1);
        int countMin = LoadUtils.attributeInt(element, "countMin", count);
        int countMax = LoadUtils.attributeInt(element, "countMax", count);
        String modTable = LoadUtils.attribute(element, "modTable", null);
        float modChance = LoadUtils.attributeFloat(element, "modChance", 1.0f);
        return new LootTableEntry(referenceID, isTable, chance, countMin, countMax, modTable, modChance);
    }

}
