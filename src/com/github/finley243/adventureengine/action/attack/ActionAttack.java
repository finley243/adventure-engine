package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandomEach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.item.template.WeaponTemplate;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.Map;
import java.util.Set;

public abstract class ActionAttack extends ActionRandomEach<AttackTarget> {

    private final Set<AttackTarget> targets;
    private final ItemWeapon weapon;
    private final Limb limb;
    private final String prompt;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final int ammoConsumed;
    private final boolean overrideWeaponRate;
    private final float damageMult;
    private final float hitChanceMult;

    public ActionAttack(ItemWeapon weapon, Set<AttackTarget> targets, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult, boolean canDodge) {
        super(ActionDetectionChance.HIGH, targets);
        this.weapon = weapon;
        this.targets = targets;
        this.limb = limb;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
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

    public Set<AttackTarget> getTargets() {
        return targets;
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

    public abstract float chance(Actor subject, AttackTarget target);

    @Override
    public float chanceOverall(Actor subject) {
        return 1.0f;
    }

    @Override
    public boolean onStart(Actor subject, int repeatActionCount) {
        for (AttackTarget target : targets) {
            if (target instanceof Actor) {
                subject.triggerScript("on_attack", (Actor) target);
            } else {
                subject.triggerScript("on_attack", subject);
            }
        }
        if(getWeapon().getClipSize() > 0) {
            getWeapon().consumeAmmo(ammoConsumed());
        }
        return true;
    }

    @Override
    public void onEnd(Actor subject, int repeatActionCount) {
        // TODO - Use illegal action system to add targets if detected
        for (AttackTarget target : targets) {
            if (target instanceof Actor && ((Actor) target).targetingComponent() != null) {
                ((Actor) target).targetingComponent().addCombatant(subject);
            }
        }
    }

    @Override
    public void onSuccess(Actor subject, AttackTarget target, int repeatActionCount) {
        int damage = damage();
        if(MathUtils.randomCheck(WeaponTemplate.CRIT_CHANCE)) {
            // No indication of critical hit to player, only damage increase
            damage += getWeapon().getCritDamage();
        }
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", (Noun) target).put("weapon", getWeapon()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitPhrase(repeatActionCount)), attackContext, this, subject));
        Damage damageData = new Damage(Damage.DamageType.PHYSICAL, damage, getLimb(), 1.0f);
        target.damage(damageData);
        subject.triggerEffect("on_attack_success");
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", (Noun) target).put("weapon", getWeapon()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissPhrase(repeatActionCount)), attackContext, this, subject));
        subject.triggerEffect("on_attack_failure");
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {}

    public int ammoConsumed() {
        return ammoConsumed;
    }

    public String getHitPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? hitPhraseRepeat : hitPhrase;
    }

    public String getMissPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? missPhraseRepeat : missPhrase;
    }

    public AreaLink.DistanceCategory getRange() {
        return getWeapon().getRange();
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (getWeapon().getClipSize() == 0 || getWeapon().getAmmoRemaining() >= ammoConsumed());
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
        if (subject.targetingComponent() == null) return 0.0f;
        for (AttackTarget target : targets) {
            if (target instanceof Actor && subject.targetingComponent().isCombatant((Actor) target)) {
                return 0.8f;
            }
        }
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
