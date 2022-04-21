package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.template.ItemTemplate;
import com.github.finley243.adventureengine.world.item.template.MiscTemplate;

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
