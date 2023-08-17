package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataMove;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentLink;

public class ActionMoveLink extends ActionMove {

	private final ObjectComponentLink linkComponent;

	public ActionMoveLink(ObjectComponentLink linkComponent) {
		this.linkComponent = linkComponent;
	}

	@Override
	public Area getDestinationArea() {
		return linkComponent.getLinkedObject().getArea();
	}

	public ObjectComponentLink getLinkComponent() {
		return linkComponent;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Area lastArea = subject.getArea();
		Area area = getDestinationArea();
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("door", linkComponent.getObject()).put("room", area.getRoom()).build());
		subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get("moveThrough"), context, this, null, subject, null));
		subject.game().eventQueue().addToEnd(new SensoryEvent(area, Phrases.get("moveThroughReverse"), context, this, null, subject, null));
		subject.setArea(area);
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (!linkComponent.movableConditionIsMet(subject)) {
			// TODO - Add custom condition reason text
			return new CanChooseResult(false, "LINK CONDITION NOT MET");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public float utility(Actor subject) {
		return UtilityUtils.getMovementUtility(subject, getDestinationArea()) * UtilityUtils.MOVE_UTILITY_MULTIPLIER;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataObject(linkComponent.getObject());
	}

	@Override
	public String getPrompt(Actor subject) {
		return "Enter";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionMoveLink other)) {
            return false;
        } else {
			return other.linkComponent == this.linkComponent;
        }
    }

}
