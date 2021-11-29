package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionEndMulti extends Action {

	public ActionEndMulti() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endMultiAction();
	}

	@Override
	public float utility(Actor subject) {
		Action highestUtilityAction = subject.chooseAction(subject.availableActions());
		if(highestUtilityAction instanceof ActionEndMulti) {
			return highestUtilityAction.utility(subject) - 0.00001f;
		} else {
			return 0.00001f;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("End action", "End action", canChoose(subject));
	}

	@Override
    public boolean equals(Object o) {    
        return o instanceof ActionEndMulti;
    }

}
