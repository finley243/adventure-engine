package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    private final Area area;
    private final ItemWeapon weapon;

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, Actor.Skill skill, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<Effect> targetEffects, float hitChanceMult, boolean canDodge) {
        super(weapon, area.getAttackTargets(), null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, skill, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge);
        this.area = area;
        this.weapon = weapon;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        return CombatHelper.calculateHitChance(subject, target, getLimb(), getSkill(), weapon.getBaseHitChanceMin(), weapon.getBaseHitChanceMax(), weapon.getAccuracyBonus(), canDodge(), hitChanceMult());
    }

    @Override
    public void consumeAmmo() {
        if(weapon.getClipSize() > 0) {
            weapon.consumeAmmo(getAmmoConsumed());
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject)
                && (weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
                && getRanges().contains(subject.getArea().getDistanceTo(area.getID())) && subject.getArea().isVisible(subject, area.getID());
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(area.getRelativeName(subject.getArea())) + " (" + subject.getArea().getRelativeDirection(subject.getArea()) + ", " + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt()});
    }

}
