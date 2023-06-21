package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionModInstall;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.template.ModTemplate;

import java.util.List;

public class ItemMod extends Item {

    public ItemMod(Game game, String ID, String templateID) {
        super(game, ID, templateID);
    }

    private ModTemplate getModTemplate() {
        return (ModTemplate) getTemplate();
    }

    public String getModSlot() {
        return getModTemplate().getModSlot();
    }

    public List<String> getEffects() {
        return getModTemplate().getEffects();
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
