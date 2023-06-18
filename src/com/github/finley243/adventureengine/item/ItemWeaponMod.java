package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.WeaponModTemplate;

public class ItemWeaponMod extends Item {

    private final String templateID;

    public ItemWeaponMod(Game game, String ID, String templateID) {
        super(game, ID);
        this.templateID = templateID;
    }

    @Override
    public ItemTemplate getTemplate() {
        return getWeaponModTemplate();
    }

    public WeaponModTemplate getWeaponModTemplate() {
        return (WeaponModTemplate) game().data().getItem(templateID);
    }

}
