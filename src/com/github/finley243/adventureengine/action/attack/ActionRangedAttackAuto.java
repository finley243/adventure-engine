package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.reaction.ActionReaction;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;

import java.util.ArrayList;
import java.util.List;

public class ActionRangedAttackAuto extends ActionAttack {

	private static final int AMMO_USED = 6;
	private static final float AUTOFIRE_DAMAGE_MULT = 3.00f;
	private static final float AUTOFIRE_HIT_CHANCE_MULT = -0.50f;

	public ActionRangedAttackAuto(ItemWeapon weapon, Actor target) {
		super(weapon, target, null);
	}

	@Override
	public List<ActionReaction> getReactions(Actor subject) {
		return new ArrayList<>();
	}

	@Override
	public int ammoConsumed() {
		return AMMO_USED;
	}

	@Override
	public int damage() {
		return (int) (getWeapon().getDamage() * AUTOFIRE_DAMAGE_MULT);
	}

	@Override
	public String getTelegraphPhrase() {
		return "rangedTelegraph";
	}

	@Override
	public String getHitPhrase() {
		return "rangedAutoHit";
	}

	@Override
	public String getMissPhrase() {
		return "rangedAutoMiss";
	}

	@Override
	public float hitChanceMult() {
		return AUTOFIRE_HIT_CHANCE_MULT;
	}

	@Override
	public int repeatCount(Actor subject) {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Autofire (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName()});
	}

}
