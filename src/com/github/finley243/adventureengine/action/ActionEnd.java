package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionEnd extends Action {

	public ActionEnd() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endTurn();
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
	public MenuData getMenuData(Actor subject) {
		return new MenuData("End turn", canChoose(subject));
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionEnd;
    }

}
