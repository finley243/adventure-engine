package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionReactionCounter extends ActionReaction {

    public ActionReactionCounter(Actor attacker, ItemWeapon weapon) {
        super(attacker, weapon);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Counter (" + getChanceTag(subject) + ")", canChoose(subject));
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && subject.equipmentComponent().hasMeleeWeaponEquipped();
    }

    @Override
    public void onSuccess(Actor subject) {
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("counterSuccess"), reactionContext, this, subject));
    }

    @Override
    public void onFail(Actor subject) {
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("counterFail"), reactionContext, this, subject));
    }

    @Override
    public float chance(Actor subject) {
        return MathUtils.chanceLinearSkillContest(subject, Actor.Skill.MELEE, attacker, Actor.Skill.MELEE, 0.01f, 0.7f);
    }

    @Override
    public float utility(Actor subject) {
        return 0.2f;
    }

}
