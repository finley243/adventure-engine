package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class ActionEnd extends Action {

	private final Runnable onEndTurn;

	public ActionEnd(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher, Runnable onEndTurn) {
        super(scriptRuntime, sensoryEventDispatcher);
		this.onEndTurn = onEndTurn;
    }

	@Override
	public String getID() {
		return "end_turn";
	}

	@Override
	public Context getContext(Actor subject) {
		return Context.builder().subject(subject).build();
	}

	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.endTurn();
		onEndTurn.run();
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
