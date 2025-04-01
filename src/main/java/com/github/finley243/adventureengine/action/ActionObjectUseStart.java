package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

public class ActionObjectUseStart extends Action {

	private final ObjectComponentUsable component;
	private final String slotID;

	public ActionObjectUseStart(ObjectComponentUsable component, String slotID) {
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
		return "object_use_start";
	}

	@Override
	public Context getContext(Actor subject) {
		Context context = new Context(subject.game(), subject, null, component.getObject());
		context.setLocalVariable("slot", Expression.constant(slotID));
		return context;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		if (subject.isUsingObject()) {
			subject.getUsingObject().object().getComponentOfType(ObjectComponentUsable.class).removeUser(subject.getUsingObject().slot());
		}
		if (component.userIsInCover(slotID)) {
			subject.triggerScript("on_take_cover", new Context(subject.game(), subject, subject, getComponent().getObject()));
		}
		component.setUser(slotID, subject);
		subject.setUsingObject(new ObjectComponentUsable.ObjectUserData(component.getObject(), slotID));
		Context context = new Context(subject.game(), subject, null, component.getObject());
		SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get(component.getStartPhrase(slotID)), context, true, this, null));
	}

	@Override
	public float utility(Actor subject) {
		if (component.userIsInCover(slotID)) {
			return UtilityUtils.getCoverUtility(subject);
		}
		return 0.0f;
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataObject(component.getObject());
	}

	@Override
	public String getPrompt(Actor subject) {
		return component.getStartPrompt(slotID);
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionObjectUseStart other)) {
            return false;
        } else {
			return other.component == this.component;
        }
    }

}
