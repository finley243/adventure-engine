package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

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
	public MenuData getMenuData(Actor subject) {
		return new MenuDataSelf();
	}

	@Override
	public String getPrompt(Actor subject) {
		return "End Turn";
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionEnd;
    }

}
