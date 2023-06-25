package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
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
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("moveThrough"), context, this, null, subject, null));
		subject.game().eventBus().post(new SensoryEvent(area, Phrases.get("moveThroughReverse"), context, this, null, subject, null));
		subject.setArea(area);
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (linkComponent.getCondition() != null && !linkComponent.getCondition().isMet(new Context(subject.game(), subject, subject, linkComponent.getObject()))) {
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
	public MenuChoice getMenuChoices(Actor subject) {
		String[] menuPath;
		if (linkComponent.getName() != null) {
			menuPath = new String[] {LangUtils.titleCase(linkComponent.getObject().getName()), LangUtils.titleCase(linkComponent.getName())};
		} else {
			menuPath = new String[] {LangUtils.titleCase(linkComponent.getObject().getName())};
		}
		return new MenuChoice("(" + linkComponent.getDirection() + ") " + "Enter", canChoose(subject).canChoose(), menuPath, new String[]{"enter " + linkComponent.getObject().getName(), "go through " + linkComponent.getObject().getName(), "move through " + linkComponent.getObject().getName()});
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
