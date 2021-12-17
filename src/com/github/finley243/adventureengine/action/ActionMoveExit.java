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
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionMoveExit extends Action {

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
		Context context = new Context(subject, false, exit, false, area.getRoom(), false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("moveThroughTo"), context, this, subject));
		Game.EVENT_BUS.post(new VisualEvent(area, Phrases.get("moveThroughTo"), context, this, subject));
		exit.unlock();
		subject.move(area);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && !exit.isLocked();
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, exit.getLinkedArea()) * ActionMove.MOVE_UTILITY_MULTIPLIER;
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
		return new MenuData(LangUtils.titleCase(exit.getName()), "Go through " + exit.getFormattedName(false) + " to " + exit.getLinkedArea().getRoom().getFormattedName(false), canChoose(subject), new String[]{"move"});
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
