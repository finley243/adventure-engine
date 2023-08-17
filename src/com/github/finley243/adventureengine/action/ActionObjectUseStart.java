package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataObject;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

public class ActionObjectUseStart extends Action {

	private final ObjectComponentUsable component;

	public ActionObjectUseStart(ObjectComponentUsable component) {
		this.component = component;
	}

	public ObjectComponentUsable getComponent() {
		return component;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		if (subject.isUsingObject()) {
			subject.getUsingObject().removeUser();
		}
		if (component.userIsInCover()) {
			subject.triggerScript("on_take_cover", new Context(subject.game(), subject, subject, getComponent().getObject()));
		}
		component.setUser(subject);
		subject.setUsingObject(component);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("object", component.getObject()).build());
		subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get(component.getStartPhrase()), context, this, null, subject, null));
		subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
	}

	@Override
	public float utility(Actor subject) {
		if (component.userIsInCover()) {
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
		return component.getStartPrompt();
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
