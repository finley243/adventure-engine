package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReactionBlock extends ActionReaction {

    public ActionReactionBlock(Actor attacker, ItemWeapon weapon) {
        super(attacker, weapon);
    }

    @Override
    public boolean cancelsAttack() {
        return true;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Block (" + getChanceTag(subject) + ")", canChoose(subject));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled && subject.equipmentComponent().hasMeleeWeaponEquipped();
    }

    @Override
    public String successPhrase() {
        return "blockSuccess";
    }

    @Override
    public String failPhrase() {
        return "blockFail";
    }

    @Override
    public float chance(Actor subject) {
        // TODO - Attribute-based chance computation
        return 0.5f;
    }

}
