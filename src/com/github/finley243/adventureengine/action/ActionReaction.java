package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionReaction extends Action {

	public enum ReactionType {
		BLOCK, DODGE
	}

	private final ReactionType type;
	
	public ActionReaction(ReactionType type) {
		this.type = type;
	}
	
	public ReactionType getType() {
		return type;
	}

	@Override
	public void choose(Actor subject) {

	}

	@Override
	public boolean usesAction() {
		return false;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		switch(type) {
		case BLOCK:
			return new MenuDataGlobal("Block", "Block", canChoose(subject));
		case DODGE:
			return new MenuDataGlobal("Dodge", "Dodge", canChoose(subject));
		default:
			return null;
		}
	}

}
