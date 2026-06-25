package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

public class ActionEnd extends Action {

	private final Runnable onEndTurn;

	public ActionEnd(Actor subject, ActionDependencies dependencies, Runnable onEndTurn) {
        super(subject, dependencies);
		this.onEndTurn = onEndTurn;
    }

	@Override
	public String getID() {
		return "end_turn";
	}

	@Override
	public Context getContext() {
		return Context.builder().subject(subject).build();
	}

	@Override
	public void choose(int repeatActionCount) {
		subject.endTurn();
		onEndTurn.run();
	}

	@Override
	public float utility() {
		return 0.00001f;
	}

	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataSelf();
	}

	@Override
	public String getPrompt() {
		return "End Turn";
	}

	@Override
    public boolean equals(Object o) {
        return o instanceof ActionEnd;
    }

}
