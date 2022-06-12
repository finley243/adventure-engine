package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.reaction.ActionReaction;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.item.ItemWeapon;

import java.util.ArrayList;
import java.util.List;

public class ActionRangedAttackTargeted extends ActionAttack {

	public ActionRangedAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		super(weapon, target, limb);
	}

	@Override
	public String getTelegraphPhrase() {
		return "rangedTelegraph";
	}

	@Override
	public String getHitPhrase() {
		return "rangedHitLimb";
	}

	@Override
	public String getMissPhrase() {
		return "rangedMissLimb";
	}

	@Override
	public int repeatCount(Actor subject) {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName(), "Targeted Attack"});
	}

}
