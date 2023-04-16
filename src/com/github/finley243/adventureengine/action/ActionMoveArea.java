package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.*;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
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

	@Override
	public Area getDestinationArea() {
		return area;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Area lastArea = subject.getArea();
		Noun moveLocation;
		if (!area.getRoom().equals(subject.getArea().getRoom())) {
			moveLocation = area.getRoom();
		} else {
			moveLocation = area;
		}
		Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("area", moveLocation).build());
		subject.game().eventBus().post(new SensoryEvent(new Area[]{subject.getArea(), area}, link.getMovePhrase(subject.getArea()), context, this, null, subject, null));
		subject.setArea(area);
	}
	
	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, area, false) * MOVE_UTILITY_MULTIPLIER;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(LangUtils.titleCase(link.getMoveName(subject.getArea())), canChoose(subject), new String[]{"move"}, new String[]{"move " + area.getName(), "go " + area.getName(), "walk " + area.getName(), "run " + area.getName(), "move to " + area.getName(), "go to " + area.getName(), "walk to " + area.getName(), "run to " + area.getName(), "move towards " + area.getName(), "go towards " + area.getName(), "walk towards " + area.getName(), "run towards " + area.getName(), "move toward " + area.getName(), "go toward " + area.getName(), "walk toward " + area.getName(), "run toward " + area.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionMoveArea other)) {
            return false;
        } else {
			return other.area == this.area;
        }
    }
	
}
