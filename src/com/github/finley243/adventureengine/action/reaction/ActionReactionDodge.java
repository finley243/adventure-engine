package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReactionDodge extends ActionReaction {

    public ActionReactionDodge(Actor attacker, ItemWeapon weapon) {
        super(attacker, weapon);
    }

    @Override
    public boolean cancelsAttack() {
        return true;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Dodge (" + getChanceTag(subject) + ")", canChoose(subject));
    }

    @Override
    public void onSuccess(Actor subject) {
        Context reactionContext = new Context(subject, attacker, weapon);
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("dodgeSuccess"), reactionContext, this, subject));
    }

    @Override
    public void onFail(Actor subject) {
        Context reactionContext = new Context(subject, attacker, weapon);
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("dodgeFail"), reactionContext, this, subject));
    }

    @Override
    public float chance(Actor subject) {
        return MathUtils.chanceLinearAttribute(subject, Actor.Attribute.AGILITY, 0.02f, 0.6f);
    }

    @Override
    public float utility(Actor subject) {
        return 0.2f;
    }

}
