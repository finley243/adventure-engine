package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.MiscTemplate;

public class ItemMisc extends Item {

    private final MiscTemplate stats;

    public ItemMisc(Game game, String ID, MiscTemplate stats) {
        super(game, ID);
        this.stats = stats;
    }

    @Override
    public ItemTemplate getTemplate() {
        return stats;
    }

}
