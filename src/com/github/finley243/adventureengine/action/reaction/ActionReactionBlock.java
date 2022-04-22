package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemWeapon;

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
        return super.canChoose(subject) && subject.equipmentComponent().hasMeleeWeaponEquipped();
    }

    @Override
    public void onSuccess(Actor subject) {
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("blockSuccess"), reactionContext, this, subject));
    }

    @Override
    public void onFail(Actor subject) {
        Context reactionContext = new Context(new NounMapper().put("actor", subject).put("attacker", attacker).put("weapon", weapon).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("blockFail"), reactionContext, this, subject));
    }

    @Override
    public float chance(Actor subject) {
        return MathUtils.chanceLinearSkill(subject, Actor.Skill.MELEE, 0.1f, 0.6f);
    }

    @Override
    public float utility(Actor subject) {
        return 0.2f;
    }

}
