package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;

public class ActionEnd extends Action {

	public ActionEnd() {}

	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.endTurn();
	}

	@Override
	public float utility(Actor subject) {
		return 0.00001f;
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("End turn", canChoose(subject).canChoose(), new String[]{"end turn", "end", "wait"});
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionEnd;
    }

}
