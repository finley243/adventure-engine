package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ActionMoveArea extends ActionMove {
	
	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;

	private final Area area;
	private final AreaLink link;

	public ActionMoveArea(Area area, AreaLink link) {
		this.area = area;
		this.link = link;
	}
	
	public Area getArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject) {
		Noun moveLocation;
		if (!area.getRoom().equals(subject.getArea().getRoom())) {
			moveLocation = area.getRoom();
		} else {
			moveLocation = area;
		}
		Context context = new Context(new NounMapper().put("actor", subject).put("area", moveLocation).build());
		subject.game().eventBus().post(new SensoryEvent(new Area[]{subject.getArea(), area}, link.getMovePhrase(subject.getArea()), context, this, subject));
		subject.setArea(area);
	}
	
	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, area, false) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(link.getMoveName(subject.getArea())), canChoose(subject), new String[]{"move"});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionMoveArea)) {
            return false;
        } else {
            ActionMoveArea other = (ActionMoveArea) o;
            return other.area == this.area;
        }
    }
	
}
