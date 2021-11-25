package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;

public class ActionWait implements Action {

	private boolean disabled;

	public ActionWait() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endTurn();
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
	public String getPrompt() {
		return "End turn";
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
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataGlobal("End turn", canChoose(subject));
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionWait;
    }

}
