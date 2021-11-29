package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;
import com.github.finley243.adventureengine.menu.data.MenuDataNested;

public class ActionMultiEnd extends Action {

	public ActionMultiEnd() {

	}

	@Override
	public void choose(Actor subject) {
		subject.endMultiAction();
	}

	@Override
	public float utility(Actor subject) {
		Action highestUtilityAction = subject.chooseAction(subject.availableActions());
		if(highestUtilityAction instanceof ActionMultiEnd) {
			return highestUtilityAction.utility(subject) - 0.00001f;
		} else {
			return 0.00001f;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataNested("End action", "End action", canChoose(subject));
	}

	@Override
    public boolean equals(Object o) {    
        return o instanceof ActionMultiEnd;
    }

}
