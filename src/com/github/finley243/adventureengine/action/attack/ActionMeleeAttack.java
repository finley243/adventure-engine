package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.reaction.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.item.ItemWeapon;

import java.util.ArrayList;
import java.util.List;

public class ActionMeleeAttack extends ActionAttack {

	public ActionMeleeAttack(ItemWeapon weapon, Actor target) {
		super(weapon, target, null);
	}

	@Override
	public List<ActionReaction> getReactions(Actor subject) {
		List<ActionReaction> reactions = new ArrayList<>();
		reactions.add(new ActionReactionBlock(subject, getWeapon()));
		reactions.add(new ActionReactionDodge(subject, getWeapon()));
		reactions.add(new ActionReactionCounter(subject, getWeapon()));
		reactions.add(new ActionReactionNone(subject, getWeapon()));
		return reactions;
	}

	@Override
	public String getTelegraphPhrase() {
		return "meleeTelegraph";
	}

	@Override
	public String getHitPhrase() {
		return "meleeHit";
	}

	@Override
	public String getMissPhrase() {
		return "meleeMiss";
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(action instanceof ActionMeleeAttack) {
			return ((ActionMeleeAttack) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName()});
	}

}
