package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandom;
import com.github.finley243.adventureengine.action.reaction.ActionReaction;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionAttack extends ActionRandom {

    private final Actor target;
    private final ItemWeapon weapon;
    private final Limb limb;
    private ActionReaction reaction;
    private boolean reactionSuccess;

    public ActionAttack(ItemWeapon weapon, Actor target, Limb limb) {
        this.weapon = weapon;
        this.target = target;
        this.limb = limb;
    }

    public ItemWeapon getWeapon() {
        return weapon;
    }

    public Actor getTarget() {
        return target;
    }

    public Limb getLimb() {
        return limb;
    }

    public int damage() {
        return weapon.getDamage();
    }

    public float hitChanceMult() {
        return 0.0f;
    }

    @Override
    public float chance(Actor subject) {
        if(reaction != null && !reactionSuccess && reaction.guaranteedHitOnFail()) {
            return 1.0f;
        }
        float chance = CombatHelper.calculateHitChance(subject, getTarget(), getLimb(), getWeapon(), hitChanceMult());
        if(reaction != null) {
            if(reactionSuccess) {
                chance *= (1.0f + reaction.getHitChanceMultOnSuccess());
            } else {
                chance *= (1.0f + reaction.getHitChanceMultOnFail());
            }
        }
        return chance;
    }

    @Override
    public boolean onStart(Actor subject) {
        subject.triggerScript("on_attack");
        if(getTarget().targetingComponent() != null) {
            getTarget().targetingComponent().addCombatant(subject);
        }
        if(getWeapon().getClipSize() > 0) {
            getWeapon().consumeAmmo(ammoConsumed());
        }
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", getTarget()).put("weapon", getWeapon()).build());
        //if(!CombatHelper.isRepeat(attackContext)) {
            subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getTelegraphPhrase()), attackContext, this, subject));
        //}
        this.reaction = chooseReaction(subject);
        this.reactionSuccess = (reaction != null && reaction.computeSuccess(getTarget()));
        if(reaction != null) {
            if(reactionSuccess) {
                reaction.onSuccess(getTarget());
            } else {
                reaction.onFail(getTarget());
            }
        }
        return !(reaction != null && reactionSuccess && reaction.cancelsAttack());
    }

    @Override
    public void onSuccess(Actor subject) {
        int damage = damage();
        if(ThreadLocalRandom.current().nextFloat() < WeaponTemplate.CRIT_CHANCE) {
            // No indication of critical hit to player, only damage increase
            damage += getWeapon().getCritDamage();
        }
        if(reaction != null) {
            if(reactionSuccess) {
                damage *= (1.0f + reaction.getDamageMultOnSuccess());
            } else {
                damage *= (1.0f + reaction.getDamageMultOnFail());
            }
        }
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", getTarget()).put("weapon", getWeapon()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitPhrase()), attackContext, this, subject));
        Damage damageData = new Damage(Damage.DamageType.PHYSICAL, damage, 1.0f);
        target.damage(damageData, getLimb());
        subject.triggerEffect("on_attack_success");
    }

    @Override
    public void onFail(Actor subject) {
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", getTarget()).put("weapon", getWeapon()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissPhrase()), attackContext, this, subject));
        subject.triggerEffect("on_attack_failure");
    }

    public int ammoConsumed() {
        return 1;
    }

    public abstract String getTelegraphPhrase();

    public abstract String getHitPhrase();

    public abstract String getMissPhrase();

    private ActionReaction chooseReaction(Actor subject) {
        List<ActionReaction> reactions = getReactions(subject);
        if(reactions != null && !reactions.isEmpty()) {
            return (ActionReaction) target.chooseAction(new ArrayList<>(reactions));
        }
        return null;
    }

    public abstract List<ActionReaction> getReactions(Actor subject);

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) &&
                (getWeapon().isRanged() || subject.getArea() == getTarget().getArea()) &&
                subject.canSee(getTarget()) &&
                (getWeapon().getClipSize() == 0 || getWeapon().getAmmoRemaining() >= ammoConsumed());
    }

    @Override
    public int repeatCount(Actor subject) {
        return weapon.getRate();
    }

    @Override
    public float utility(Actor subject) {
        if (subject.targetingComponent() != null && subject.targetingComponent().isCombatant(getTarget())) return 0.8f;
        return 0.0f;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        if(action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeapon() == this.getWeapon();
        } else {
            return false;
        }
    }

}
