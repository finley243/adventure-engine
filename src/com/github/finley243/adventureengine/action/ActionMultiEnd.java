package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionMultiEnd implements Action {

	public ActionMultiEnd() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endMultiAction();
		//Context context = new Context(subject, false);
		//Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("idle"), context));
	}

	@Override
	public String getPrompt() {
		return "End action";
	}

	@Override
	public float utility(Actor subject) {
		return 0.00001f;
	}
	
	@Override
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataGlobal("End action");
	}

}
