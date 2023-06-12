package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspectObject extends Action {

	private final WorldObject object;

	public ActionInspectObject(WorldObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		SceneManager.trigger(subject.game(), subject, subject, object.getDescription());
		object.triggerScript("on_inspect", subject, subject);
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Inspect", canChoose(subject), new String[]{LangUtils.titleCase(object.getName())}, new String[]{"inspect " + object.getName(), "examine " + object.getName(), "look at " + object.getName()});
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
