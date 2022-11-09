package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

import java.util.Set;

public class ObjectComponentTemplateItemUse extends ObjectComponentTemplate {

    private final Set<String> itemIDs;
    private final boolean consumeItem;

    public ObjectComponentTemplateItemUse(Game game, String ID, boolean startEnabled, Set<String> itemIDs, boolean consumeItem) {
        super(game, ID, startEnabled);
        this.itemIDs = itemIDs;
        this.consumeItem = consumeItem;
    }

    public Set<String> getItemIDs() {
        return itemIDs;
    }

    public boolean consumeItem() {
        return consumeItem;
    }

}
