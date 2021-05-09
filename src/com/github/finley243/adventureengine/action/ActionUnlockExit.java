package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionUnlockExit implements Action {

	private ObjectExit exit;
	
	public ActionUnlockExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		exit.unlock();
		Context context = new Context(subject, exit);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("unlock"), context));
	}

	@Override
	public String getPrompt() {
		return "Unlock " + exit.getFormattedName() + " to " + exit.getLinkedArea().getRoom().getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.titleCase(exit.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
}
