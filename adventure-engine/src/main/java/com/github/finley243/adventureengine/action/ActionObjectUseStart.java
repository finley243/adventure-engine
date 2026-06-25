package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.world.object.component.UsableObjectComponent;

public class ActionObjectUseStart extends Action {

	private final UsableObjectComponent component;
	private final String slotID;

	public ActionObjectUseStart(Actor subject, ActionDependencies dependencies, UsableObjectComponent component, String slotID) {
        super(subject, dependencies);
        this.component = component;
		this.slotID = slotID;
	}

	public UsableObjectComponent getComponent() {
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
	public Context getContext() {
		Context context = Context.builder().subject(subject).parentObject(component.getObject()).parentAction(this).build();
		context.setLocalVariable("slot", Expression.string(slotID));
		return context;
	}
	
	@Override
	public void choose(int repeatActionCount) {
		if (subject.isPlayer()) {
			component.getObject().setKnown();
		}
		Context context = getContext();
		if (subject.isUsingObject()) {
			subject.getUsingObject().object().getComponentOfType(UsableObjectComponent.class).removeUser(subject.getUsingObject().slot());
		}
		if (component.userIsInCover(slotID)) {
			subject.triggerScript("on_take_cover", context);
		}
		component.setUser(slotID, subject);
		subject.setUsingObject(new UsableObjectComponent.ObjectUserData(component.getObject(), slotID));
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), component.getStartPhrase(slotID), context, true, this, null));
	}

	@Override
	public float utility() {
		if (component.userIsInCover(slotID)) {
			return UtilityUtils.getCoverUtility(subject);
		}
		return 0.0f;
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataObject(component.getObject());
	}

	@Override
	public String getPrompt() {
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
