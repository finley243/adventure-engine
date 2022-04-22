package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.reaction.ActionReaction;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.item.ItemWeapon;

import java.util.ArrayList;
import java.util.List;

public class ActionRangedAttack extends ActionAttack {
	
	public ActionRangedAttack(ItemWeapon weapon, Actor target) {
		super(weapon, target, null);
	}

	@Override
	public List<ActionReaction> getReactions(Actor subject) {
		return new ArrayList<>();
	}

	@Override
	public String getTelegraphPhrase() {
		return "rangedTelegraph";
	}

	@Override
	public String getHitPhrase() {
		return "rangedHit";
	}

	@Override
	public String getMissPhrase() {
		return "rangedMiss";
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(action instanceof ActionRangedAttack) {
			return ((ActionRangedAttack) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName()});
	}

}
