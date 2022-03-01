package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReactionBlock extends ActionReaction {

    public ActionReactionBlock(Actor target, ItemWeapon weapon) {
        super(target, weapon);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Block", true, new String[0]);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return !disabled && subject.hasMeleeWeaponEquipped();
    }

    @Override
    public float onSuccess(Actor subject) {
        Context reactionContext = new Context(subject, attacker, weapon);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockSuccess"), reactionContext, null, null));
        return 0.0f;
    }

    @Override
    public float onFail(Actor subject) {
        Context reactionContext = new Context(subject, attacker, weapon);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("blockFail"), reactionContext, null, null));
        return 1.0f;
    }

    @Override
    public float chance(Actor subject) {
        return 0.30f;
    }

}
