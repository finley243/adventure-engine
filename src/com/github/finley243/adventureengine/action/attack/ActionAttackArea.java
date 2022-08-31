package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ActionAttackArea extends ActionAttack {

    private final Area area;
    private final ItemWeapon weapon;

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, AreaLink.DistanceCategory range, int rate, int damage, Damage.DamageType damageType, float armorMult, float hitChanceMult, boolean canDodge) {
        super(weapon, area.getAttackTargets(), null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, ammoConsumed, range, rate, damage, damageType, armorMult, hitChanceMult, canDodge);
        this.area = area;
        this.weapon = weapon;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        return CombatHelper.calculateHitChance(subject, target, getLimb(), weapon, weapon.getBaseHitChanceMin(), weapon.getBaseHitChanceMax(), canDodge(), hitChanceMult());
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
                && subject.getArea().getDistanceTo(area.getID()) == getRange() && subject.getArea().isVisible(subject, area.getID());
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), area.getName()});
    }

}
