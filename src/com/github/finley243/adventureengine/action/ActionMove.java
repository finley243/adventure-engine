package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ActionMove extends Action {
	
	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;

	private final Area area;
	private final AreaLink.RelativeDirection direction;
	
	public ActionMove(Area area, AreaLink.RelativeDirection direction) {
		this.area = area;
		this.direction = direction;
	}
	
	public Area getArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, false, area, false);
		Game.EVENT_BUS.post(new VisualEvent(new Area[]{subject.getArea(), area}, Phrases.get(area.getMovePhrase()), context, this, subject));
		subject.move(area);
	}
	
	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, area) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public int repeatCount() {
		return 1;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return action instanceof ActionMove ||
			action instanceof ActionMoveExit ||
			action instanceof ActionMoveElevator;
	}

	@Override
	public boolean isBlockedMatch(Action action) {
		return action instanceof ActionMove ||
				action instanceof ActionMoveExit ||
				action instanceof ActionMoveElevator;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(area.getMoveDescription()) + " (" + direction.tag + ")", "Move " + area.getMoveDescription() + " (" + direction.tag + ")", canChoose(subject), new String[]{"move"});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionMove)) {
            return false;
        } else {
            ActionMove other = (ActionMove) o;
            return other.area == this.area;
        }
    }
	
}
