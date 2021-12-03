package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionReactionBlock extends ActionReaction {

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Block", "Block", true, new String[0]);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled && subject.hasMeleeWeaponEquipped();
    }

    @Override
    public float chance(Actor subject) {
        return 0.30f;
    }

    @Override
    public float damageMult() {
        return 0.00f;
    }

}
