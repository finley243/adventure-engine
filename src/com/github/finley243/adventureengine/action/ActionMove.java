package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataMove;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionMove implements Action {
	
	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;

	private boolean disabled;
	private final Area area;
	
	public ActionMove(Area area) {
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, false, area, false);
		String line;
		if(area.isProximateName()) {
			line = "moveToward";
		} else {
			line = "moveTo";
		}
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(line), context, this, subject));
		subject.move(area);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	@Override
	public void disable() {
		disabled = true;
	}
	
	@Override
	public String getPrompt() {
		return "Move to " + area.getFormattedName(false);
	}
	
	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, area) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return action instanceof ActionMove ||
			action instanceof ActionMoveExit ||
			action instanceof ActionMoveElevator;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataMove("Move", canChoose(subject), area);
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
