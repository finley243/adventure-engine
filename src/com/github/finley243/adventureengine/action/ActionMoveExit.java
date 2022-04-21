package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectDoor;

public class ActionMoveExit extends ActionMove {

	private final ObjectDoor exit;
	
	public ActionMoveExit(ObjectDoor exit) {
		this.exit = exit;
	}
	
	public Area getArea() {
		return exit.getLinkedArea();
	}

	public ObjectDoor getExit() {
		return exit;
	}
	
	@Override
	public void choose(Actor subject) {
		Area area = exit.getLinkedArea();
		Context context = new Context(new NounMapper().put("actor", subject).put("exit", exit).put("room", area.getRoom()).build());
		subject.game().eventBus().post(new SensoryEvent(new Area[]{subject.getArea(), area}, Phrases.get("moveThrough"), context, this, subject));
		exit.unlock();
		subject.setArea(area);
	}

	@Override
	public boolean canChoose(Actor subject) {
		if(!subject.equals(subject.game().data().getPlayer())) {
			if(!exit.isLocked()) return !disabled;
			for(String key : exit.getKeyIDs()) {
				if(subject.inventory().hasItem(key)) {
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
