package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.MiscTemplate;

public class ItemMisc extends Item {

    private final String templateID;

    public ItemMisc(Game game, String ID, String templateID) {
        super(game, ID);
        this.templateID = templateID;
    }

    @Override
    public ItemTemplate getTemplate() {
        return getMiscTemplate();
    }

    public MiscTemplate getMiscTemplate() {
        return (MiscTemplate) game().data().getItem(templateID);
    }

}
