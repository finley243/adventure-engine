package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.LootTable;
import com.github.finley243.adventureengine.item.LootTableEntry;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import org.w3c.dom.Element;

import java.util.*;
import java.util.function.Function;

public class LootTableLoader {

    private static final String NAME_LOOT_TABLE = "lootTable";

    private final Registry<ItemTemplate> itemTemplateRegistry;

    public LootTableLoader(Registry<ItemTemplate> itemTemplateRegistry) {
        this.itemTemplateRegistry = itemTemplateRegistry;
    }

    public Map<String, LootTable> load(Element element) {
        List<TemplateNode> templateNodes = new ArrayList<>();
        for (Element child : LoadUtils.directChildrenWithName(element, NAME_LOOT_TABLE)) {
            TemplateNode templateNode = parseTemplateNode(child);
            templateNodes.add(templateNode);
        }
        List<TemplateNode> sortedNodes = LoadUtils.topologicalSort(templateNodes, TemplateNode::id, TemplateNode::referencedTableIds);
        Map<String, LootTable> lootTableMap = new HashMap<>();
        for (TemplateNode node : sortedNodes) {
            LootTable lootTable = parseLootTable(node.element(), lootTableMap::get, false);
            lootTableMap.put(node.id(), lootTable);
        }
        return lootTableMap;
    }

    private TemplateNode parseTemplateNode(Element element) {
        String id = element.getAttribute("id");
        Set<String> referencedTableIds = new HashSet<>();
        for (Element entryElement : LoadUtils.directChildrenWithName(element, "table")) {
            referencedTableIds.add(entryElement.getTextContent());
            String modTableID = LoadUtils.attribute(entryElement, "modTable", null);
            if (modTableID != null) {
                referencedTableIds.add(modTableID);
            }
        }
        return new TemplateNode(id, referencedTableIds, element);
    }

    LootTable parseLootTable(Element element, Function<String, LootTable> lootTableLookup, boolean useAllDefault) {
        if (element == null) return null;
        String tableID = element.getAttribute("id");
        boolean useAll = LoadUtils.attributeBool(element, "useAll", useAllDefault);
        List<Element> entryItems = LoadUtils.directChildrenWithName(element, "item");
        List<Element> entryTables = LoadUtils.directChildrenWithName(element, "table");
        List<LootTableEntry> entries = new ArrayList<>();
        for (Element entryItem : entryItems) {
            LootTableEntry entry = parseLootTableEntry(entryItem, lootTableLookup, false);
            entries.add(entry);
        }
        for (Element entryTable : entryTables) {
            LootTableEntry entry = parseLootTableEntry(entryTable, lootTableLookup, true);
            entries.add(entry);
        }
        return new LootTable(tableID, useAll, entries);
    }

    private LootTableEntry parseLootTableEntry(Element element, Function<String, LootTable> lootTableLookup, boolean isTable) {
        if (element == null) return null;
        String referenceID = element.getTextContent();
        float chance = LoadUtils.attributeFloat(element, "chance", 1.0f);
        int count = LoadUtils.attributeInt(element, "count", 1);
        int countMin = LoadUtils.attributeInt(element, "countMin", count);
        int countMax = LoadUtils.attributeInt(element, "countMax", count);
        String modTableID = LoadUtils.attribute(element, "modTable", null);
        LootTable modTable = lootTableLookup.apply(modTableID);
        if (modTable == null) throw new GameDataException("LootTable has invalid mod table reference");
        float modChance = LoadUtils.attributeFloat(element, "modChance", 1.0f);
        if (isTable) {
            LootTable tableReference = lootTableLookup.apply(referenceID);
            if (tableReference == null) throw new GameDataException("LootTable has invalid sub-table reference");
            return new LootTableEntry(tableReference, chance, countMin, countMax, modTable, modChance);
        } else {
            ItemTemplate itemReference = itemTemplateRegistry.getFromID(referenceID);
            if (itemReference == null) throw new GameDataException("LootTable has invalid item reference");
            return new LootTableEntry(itemReference, chance, countMin, countMax, modTable, modChance);
        }
    }

    private record TemplateNode(String id, Collection<String> referencedTableIds, Element element) {}

}
