package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionReaction implements Action {

	public enum ReactionType {
		BLOCK, DODGE
	}
	
	private ReactionType type;
	
	public ActionReaction(ReactionType type) {
		this.type = type;
	}
	
	public ReactionType getType() {
		return type;
	}

	@Override
	public void choose(Actor subject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPrompt() {
		switch(type) {
		case BLOCK:
			return "Block";
		case DODGE:
			return "Dodge";
		default:
			return null;
		}
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean usesAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRepeat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int actionCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public MenuData getMenuData() {
		switch(type) {
		case BLOCK:
			return new MenuDataGlobal("Block");
		case DODGE:
			return new MenuDataGlobal("Dodge");
		default:
			return null;
		}
	}

}
