package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.CombatTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectExit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionMoveExit implements Action {

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
		return !exit.isLocked();
	}

	@Override
	public String getPrompt() {
		return "Go through " + exit.getFormattedName(false) + " to " + exit.getLinkedArea().getRoom().getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, exit.getLinkedArea()) * ActionMove.MOVE_UTILITY_MULTIPLIER;
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
		return new MenuDataWorldObject("Go through", canChoose(subject), exit);
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
