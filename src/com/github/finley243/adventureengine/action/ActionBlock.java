package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionBlock implements Action {

	public ActionBlock() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void choose(Actor subject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPrompt() {
		// TODO Auto-generated method stub
		return "Block";
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
		return 0;
	}

	@Override
	public ActionLegality getLegality() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuData getMenuData() {
		// TODO Auto-generated method stub
		return new MenuDataGlobal("Block");
	}

}
