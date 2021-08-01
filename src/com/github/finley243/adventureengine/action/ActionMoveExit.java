package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionMoveExit implements Action {

	private ObjectExit exit;
	
	public ActionMoveExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	public Area getArea() {
		return exit.getLinkedArea();
	}
	
	@Override
	public void choose(Actor subject) {
		Area area = exit.getLinkedArea();
		Context context = new Context(subject, false, exit, false, area.getRoom(), false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("moveThroughTo"), context));
		Game.EVENT_BUS.post(new VisualEvent(area, Phrases.get("moveThroughTo"), context));
		exit.unlock();
		subject.move(area);
	}

	@Override
	public String getPrompt() {
		return "Go through " + exit.getFormattedName(false) + " to " + exit.getLinkedArea().getRoom().getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"Move"};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Go through", exit);
	}

}
