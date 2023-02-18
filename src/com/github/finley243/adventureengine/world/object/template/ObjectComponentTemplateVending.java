package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

import java.util.List;

public class ObjectComponentTemplateVending extends ObjectComponentTemplate {

    private final List<String> vendingItems;

    public ObjectComponentTemplateVending(Game game, String ID, boolean startEnabled, String name, List<String> vendingItems) {
        super(game, ID, startEnabled, name);
        this.vendingItems = vendingItems;
    }

    public List<String> getVendingItems() {
        return vendingItems;
    }

}
