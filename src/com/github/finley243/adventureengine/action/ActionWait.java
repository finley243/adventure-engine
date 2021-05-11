package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionWait implements Action {

	public ActionWait() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endTurn();
		//Context context = new Context(subject, false);
		//Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("idle"), context));
	}

	@Override
	public String getPrompt() {
		return "End turn";
	}

	@Override
	public float utility(Actor subject) {
		return 0.00001f;
	}
	
	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public String[] getMenuStructure() {
		return new String[] {};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataGlobal("End turn");
	}

}
