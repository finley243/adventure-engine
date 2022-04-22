package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemWeapon;

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
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("dodgeSuccess"), reactionContext, this, subject));
    }

    @Override
    public void onFail(Actor subject) {
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("dodgeFail"), reactionContext, this, subject));
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
