package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.MiscTemplate;

public class ItemMisc extends Item {

    public ItemMisc(Game game, String ID, String templateID) {
        super(game, ID, templateID);
    }

    protected MiscTemplate getMiscTemplate() {
        return (MiscTemplate) getTemplate();
    }

}
