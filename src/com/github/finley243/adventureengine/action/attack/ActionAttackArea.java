package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    // TODO - Find way to add area to attack phrase context
    private final Area area;
    private final ItemWeapon weapon;

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<Effect> targetEffects, float hitChanceMult, boolean canDodge) {
        super(weapon, area.getAttackTargets(), null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge);
        this.area = area;
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo() {
        if (weapon != null && weapon.getClipSize() > 0) {
            if (weapon.getLoadedAmmoType().isReusable()) {
                Item.itemToObject(weapon.game(), weapon.getLoadedAmmoType(), getAmmoConsumed(), area);
            }
            weapon.consumeAmmo(getAmmoConsumed());
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject)
                && (weapon == null || weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
                && getRanges().contains(subject.getArea().getDistanceTo(area.getID())) && subject.getArea().isVisible(subject, area.getID());
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(area.getRelativeName(subject.getArea())) + " (" + subject.getArea().getRelativeDirection(subject.getArea()) + ", " + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt()});
    }

}
