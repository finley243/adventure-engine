package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    // TODO - Find way to add area to attack phrase context
    private final Area area;
    private final Item weapon;

    public ActionAttackArea(Item weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
        super(weapon, area.getAttackTargets(), null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType);
        this.area = area;
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo(Actor subject) {
        if (weapon instanceof ItemWeapon) {
            if (((ItemWeapon) weapon).getClipSize() > 0) {
                if (((ItemWeapon) weapon).getLoadedAmmoType().isReusable() && ((ItemWeapon) weapon).getLoadedAmmoType() != null) {
                    Item.itemToObject(weapon.game(), ((ItemWeapon) weapon).getLoadedAmmoType(), getAmmoConsumed(), area);
                }
                ((ItemWeapon) weapon).consumeAmmo(getAmmoConsumed());
            }
        } else {
            subject.inventory().removeItems(weapon, getAmmoConsumed());
            // TODO - Make this optional (e.g. do not place a grenade object after using a grenade)
            Item.itemToObject(weapon.game(), weapon, getAmmoConsumed(), area);
        }
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject)
                && (!(weapon instanceof ItemWeapon) || ((ItemWeapon) weapon).getClipSize() == 0 || ((ItemWeapon) weapon).getAmmoRemaining() >= getAmmoConsumed())
                && getRanges().contains(subject.getArea().getDistanceTo(area.getID())) && subject.getArea().isVisible(subject, area.getID());
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData(LangUtils.titleCase(area.getRelativeName(subject.getArea())) + " (" + subject.getArea().getRelativeDirection(subject.getArea()) + ", " + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt()});
    }

}
