package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectDoor;

public class ActionMoveDoor extends ActionMove {

	private final ObjectDoor door;
	
	public ActionMoveDoor(ObjectDoor door) {
		this.door = door;
	}

	@Override
	public Area getDestinationArea() {
		return door.getLinkedArea();
	}

	public ObjectDoor getDoor() {
		return door;
	}
	
	@Override
	public void choose(Actor subject) {
		Area lastArea = subject.getArea();
		Area area = door.getLinkedArea();
		Context context = new Context(new NounMapper().put("actor", subject).put("exit", door).put("room", area.getRoom()).build());
		subject.game().eventBus().post(new SensoryEvent(new Area[]{subject.getArea(), area}, Phrases.get("moveThrough"), context, this, subject));
		if (door.getLinkedDoor().isLocked()) {
			door.getLinkedDoor().getLock().setLocked(false);
		}
		subject.setArea(area);
		subject.onMove(lastArea);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && !door.isLocked();
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, door.getLinkedArea(), true) * ActionMoveArea.MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Enter", canChoose(subject), new String[]{door.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionMoveDoor)) {
            return false;
        } else {
            ActionMoveDoor other = (ActionMoveDoor) o;
            return other.door == this.door;
        }
    }

}
