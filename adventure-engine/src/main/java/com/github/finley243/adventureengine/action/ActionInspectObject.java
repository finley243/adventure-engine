package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspectObject extends Action {

	private final WorldObject object;

	public ActionInspectObject(Actor subject, ActionDependencies dependencies, WorldObject object) {
        super(subject, dependencies);
        this.object = object;
	}

	@Override
	public String getID() {
		return "inspect_object";
	}

	@Override
	public Context getContext() {
		return Context.builder().subject(subject).parentObject(object).parentAction(this).build();
	}
	
	@Override
	public void choose(int repeatActionCount) {
		if (subject.isPlayer()) {
			object.setKnown();
		}
		Context context = getContext();
		menuManager.sceneMenu(object.getDescription(), context, false);
		object.triggerScript("on_inspect", scriptRuntime, context);
	}

	@Override
	public int actionPoints() {
		return 0;
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataObject(object);
	}

	@Override
	public String getPrompt() {
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
