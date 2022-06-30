package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class ActionInspectObject extends Action {

	private final WorldObject object;

	public ActionInspectObject(WorldObject object) {
		super(ActionDetectionChance.NONE);
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		SceneManager.trigger(subject.game(), subject, object.getDescription());
		object.triggerScript("on_inspect", subject);
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Inspect", canChoose(subject), new String[]{object.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInspectObject)) {
            return false;
        } else {
            ActionInspectObject other = (ActionInspectObject) o;
            return other.object == this.object;
        }
    }

}
