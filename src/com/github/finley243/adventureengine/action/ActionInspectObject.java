package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
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
	public Context getContext(Actor subject) {
		return new Context(subject.game(), subject, null, object);
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Context context = new Context(subject.game(), subject, subject, object);
		subject.game().menuManager().sceneMenu(subject.game(), object.getDescription(), context);
		object.triggerScript("on_inspect", context);
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataObject(object);
	}

	@Override
	public String getPrompt(Actor subject) {
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
