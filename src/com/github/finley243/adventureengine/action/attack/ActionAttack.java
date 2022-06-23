package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.menu.MenuData;
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
    private final String prompt;
    private final String hitPhrase;
    private final String missPhrase;
    private final int ammoConsumed;
    private final boolean overrideWeaponRate;
    private final float damageMult;
    private final float hitChanceMult;
    private ActionReaction reaction;
    private boolean reactionSuccess;

    public ActionAttack(ItemWeapon weapon, Actor target, Limb limb, String prompt, String hitPhrase, String missPhrase, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult) {
        this.weapon = weapon;
        this.target = target;
        this.limb = limb;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.missPhrase = missPhrase;
        this.ammoConsumed = ammoConsumed;
        this.overrideWeaponRate = overrideWeaponRate;
        this.damageMult = damageMult;
        this.hitChanceMult = hitChanceMult;
    }

    public String getPrompt() {
        return prompt;
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
        return (int) (getWeapon().getDamage() * damageMult);
    }

    public float hitChanceMult() {
        return hitChanceMult;
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
        if(getWeapon().getClipSize() > 0) {
            getWeapon().consumeAmmo(ammoConsumed());
        }
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
    public void onEnd(Actor subject) {
        if(getTarget().targetingComponent() != null) {
            getTarget().targetingComponent().addCombatant(subject);
        }
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
        return ammoConsumed;
    }

    public String getHitPhrase() {
        return hitPhrase;
    }

    public String getMissPhrase() {
        return missPhrase;
    }

    public int getRangeMin() {
        return getWeapon().getRangeMin();
    }

    public int getRangeMax() {
        return getWeapon().getRangeMax();
    }

    private ActionReaction chooseReaction(Actor subject) {
        List<ActionReaction> reactions = getReactions(subject);
        if(reactions != null && !reactions.isEmpty()) {
            return (ActionReaction) target.chooseAction(new ArrayList<>(reactions));
        }
        return null;
    }

    public List<ActionReaction> getReactions(Actor subject) {
        return new ArrayList<>();
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) &&
                MathUtils.isInRange(subject.getArea().getDistanceTo(getTarget().getArea().getID()), getRangeMin(), getRangeMax()) &&
                subject.canSee(getTarget()) &&
                (getWeapon().getClipSize() == 0 || getWeapon().getAmmoRemaining() >= ammoConsumed());
    }

    @Override
    public int repeatCount(Actor subject) {
        if (overrideWeaponRate) {
            return 1;
        }
        return weapon.getRate();
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionAttack && action.getClass().equals(this.getClass()) && ((ActionAttack) action).getWeapon() == this.getWeapon();
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
