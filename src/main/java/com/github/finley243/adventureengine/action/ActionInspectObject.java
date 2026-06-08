package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspectObject extends Action {

	private final WorldObject object;

	public ActionInspectObject(WorldObject object) {
		this.object = object;
	}

	@Override
	public String getID() {
		return "inspect_object";
	}

	@Override
	public Context getContext(Game game, Actor subject) {
		return Context.builder(game).subject(subject).parentObject(object).parentAction(this).build();
	}
	
	@Override
	public void choose(Game game, int repeatActionCount, Actor subject) {
		if (subject.isPlayer()) {
			object.setKnown();
		}
		Context context = getContext(game, subject);
		game.menuManager().sceneMenu(game, object.getDescription(), context, false);
		object.triggerScript("on_inspect", context);
	}

	@Override
	public int actionPoints(Game game, Actor subject) {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataObject(object);
	}

	@Override
	public String getPrompt(Game game, Actor subject) {
		return "Inspect";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInspectObject other)) {
            return false;
        } else {
			return other.object == this.object;
        }
    }

}
