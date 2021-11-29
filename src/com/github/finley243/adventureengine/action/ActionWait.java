package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionWait extends Action {

	public ActionWait() {

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
	public boolean usesAction() {
		return false;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataGlobal("End turn", "End turn", canChoose(subject));
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionWait;
    }

}
