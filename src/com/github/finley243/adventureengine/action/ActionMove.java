package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataMove;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionMove implements Action {
	
	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;
	
	private Area area;
	
	public ActionMove(Area area) {
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.move(area);
		Context context = new Context(subject, false, area, false);
		String line;
		if(area.isProximateName()) {
			line = "moveToward";
		} else {
			line = "moveTo";
		}
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(line), context));
	}
	
	@Override
	public String getPrompt() {
		return "Go to " + area.getFormattedName(false);
	}
	
	@Override
	public float utility(Actor subject) {
		return subject.getMovementUtilityRank(area) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 2;
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataMove("Move", area);
	}
	
}
