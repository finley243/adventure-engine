package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionMoveExit extends ActionMove {

	private final ObjectExit exit;
	
	public ActionMoveExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	public Area getArea() {
		return exit.getLinkedArea();
	}

	public ObjectExit getExit() {
		return exit;
	}
	
	@Override
	public void choose(Actor subject) {
		Area area = exit.getLinkedArea();
		Context context = new Context(subject, exit, area.getRoom());
		subject.game().eventBus().post(new AudioVisualEvent(new Area[]{subject.getArea(), area}, Phrases.get("moveThrough"), context, this, subject));
		exit.unlock();
		subject.setArea(area);
	}

	@Override
	public boolean canChoose(Actor subject) {
		if(!subject.equals(subject.game().data().getPlayer())) {
			if(!exit.isLocked()) return !disabled;
			for(String key : exit.getKeyIDs()) {
				if(subject.inventory().hasItemWithID(key)) {
					return !disabled;
				}
			}
			return false;
		}
		return !disabled && !exit.isLocked();
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, exit.getLinkedArea(), true) * ActionMoveArea.MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Enter", canChoose(subject), new String[]{exit.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionMoveExit)) {
            return false;
        } else {
            ActionMoveExit other = (ActionMoveExit) o;
            return other.exit == this.exit;
        }
    }

}
