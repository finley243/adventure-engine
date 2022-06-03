package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionMoveArea extends ActionMove {
	
	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;

	private final Area area;

	public ActionMoveArea(Area area) {
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(new NounMapper().put("actor", subject).put("area", area).build());
		subject.game().eventBus().post(new SensoryEvent(new Area[]{subject.getArea(), area}, Phrases.get("moveToward"), context, this, subject));
		subject.setArea(area);
	}
	
	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, area, false) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(area.getName()), canChoose(subject), new String[]{"move"});
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
