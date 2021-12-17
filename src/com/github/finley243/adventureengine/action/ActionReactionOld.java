package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionReactionOld extends Action {

	public enum ReactionType {
		BLOCK, DODGE
	}

	private final ReactionType type;
	
	public ActionReactionOld(ReactionType type) {
		this.type = type;
	}
	
	public ReactionType getType() {
		return type;
	}

	@Override
	public void choose(Actor subject) {

	}

	@Override
	public boolean canChoose(Actor subject) {
		switch(type) {
		case BLOCK:
			return subject.hasMeleeWeaponEquipped();
		case DODGE:
			return subject.canMove();
		default:
			return false;
		}
	}

	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		switch(type) {
		case BLOCK:
			return new MenuData("Block", "Block", canChoose(subject));
		case DODGE:
			return new MenuData("Dodge", "Dodge", canChoose(subject));
		default:
			return null;
		}
	}

}
