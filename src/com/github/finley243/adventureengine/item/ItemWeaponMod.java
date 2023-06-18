package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModInstall;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.item.template.WeaponModTemplate;

import java.util.List;

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

    @Override
    public List<Action> inventoryActions(Actor subject) {
        List<Action> actions = super.inventoryActions(subject);
        for (Item item : subject.getInventory().getItems()) {
            if (item instanceof ItemWeapon weapon) {
                actions.add(new ActionModInstall(weapon, this));
            }
        }
        return actions;
    }

}
