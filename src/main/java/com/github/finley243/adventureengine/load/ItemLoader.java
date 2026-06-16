package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import org.w3c.dom.Element;

public class ItemLoader {

    private final ItemFactory itemFactory;
    private final Registry<ItemTemplate> itemTemplateRegistry;

    public ItemLoader(ItemFactory itemFactory, Registry<ItemTemplate> itemTemplateRegistry) {
        this.itemFactory = itemFactory;
        this.itemTemplateRegistry = itemTemplateRegistry;
    }

    Item parseItem(Element element) throws GameDataException {
        String templateID = LoadUtils.attribute(element, "template", null);
        if (templateID == null) throw new GameDataException("Item instance does not reference a template");
        ItemTemplate template = itemTemplateRegistry.getFromID(templateID);
        if (template == null) throw new GameDataException("Item instance references a nonexistent template");
        String instanceID = LoadUtils.attribute(element, "id", null);
        Item itemInstance;
        if (instanceID == null) {
            itemInstance = itemFactory.createWithGenID(template);
        } else {
            itemInstance = itemFactory.create(template, instanceID);
        }
        return itemInstance;
    }

}
