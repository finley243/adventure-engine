package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final AttackTarget target;
	private final ItemWeapon weapon;

	public ActionAttackBasic(ItemWeapon weapon, AttackTarget target, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<Effect> targetEffects, float hitChanceMult, boolean canDodge) {
		super(weapon, Set.of(target), null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo() {
		if (weapon != null && weapon.getClipSize() > 0) {
			if (weapon.getLoadedAmmoType().isReusable()) {
				Item.itemToObject(weapon.game(), weapon.getLoadedAmmoType(), getAmmoConsumed(), target.getArea());
			}
			weapon.consumeAmmo(getAmmoConsumed());
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject)
				&& (weapon == null || weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
				&& getRanges().contains(subject.getArea().getDistanceTo(target.getArea().getID()))
				&& subject.canSee(target);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(((Noun) target).getName()) + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt()});
	}

}
