package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

public class ActionObjectUseEnd extends Action {

	private final ObjectComponentUsable component;
	private final String slotID;

	public ActionObjectUseEnd(ObjectComponentUsable component, String slotID) {
		this.component = component;
		this.slotID = slotID;
	}

	public ObjectComponentUsable getComponent() {
		return component;
	}

	public String getSlotID() {
		return slotID;
	}

	@Override
	public String getID() {
		return "object_use_end";
	}

	@Override
	public Context getContext(Actor subject) {
		Context context = new Context(subject.game(), subject, null, component.getObject(), null, null, this);
		context.setLocalVariable("slot", Expression.constant(slotID));
		return context;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Context context = getContext(subject);
		if (component.userIsInCover(slotID)) {
			subject.triggerScript("on_leave_cover", context);
		}
		component.removeUser(slotID);
		subject.setUsingObject(null);
		SensoryEvent.execute(new SensoryEvent(subject.getArea(), Phrases.get(component.getEndPhrase(slotID)), context, true, this, null));
	}

	@Override
	public float utility(Actor subject) {
		if (component.userIsInCover(slotID)) {
			return 0.3f;
		}
		return 0.0f;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataObject(component.getObject());
	}

	@Override
	public String getPrompt(Actor subject) {
		return component.getEndPrompt(slotID);
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionObjectUseEnd other)) {
            return false;
        } else {
			return other.component == this.component;
        }
    }

}
