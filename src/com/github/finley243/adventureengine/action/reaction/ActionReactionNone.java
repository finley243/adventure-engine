package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReactionNone extends ActionReaction {

    public ActionReactionNone(Actor attacker, ItemWeapon weapon) {
        super(attacker, weapon);
    }

    @Override
    public void onSuccess(Actor subject) {}

    @Override
    public void onFail(Actor subject) {}

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Do nothing", canChoose(subject));
    }

    @Override
    public float chance(Actor subject) {
        return 1.0f;
    }

    @Override
    public float utility(Actor subject) {
        return 0.1f;
    }

}
