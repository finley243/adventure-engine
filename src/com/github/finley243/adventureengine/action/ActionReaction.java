package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionReaction implements Action {

	public enum ReactionType {
		BLOCK, DODGE
	}

	private boolean disabled;
	private final ReactionType type;
	
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
	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	@Override
	public void disable() {
		disabled = true;
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
