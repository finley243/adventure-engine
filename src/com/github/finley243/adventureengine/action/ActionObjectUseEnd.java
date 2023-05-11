package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentUsable;

public class ActionObjectUseEnd extends Action {

	private final ObjectComponentUsable component;

	public ActionObjectUseEnd(ObjectComponentUsable component) {
		this.component = component;
	}

	public ObjectComponentUsable getComponent() {
		return component;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		if (component.getTemplateUsable().userIsInCover()) {
			subject.triggerScript("on_leave_cover", subject);
		}
		component.removeUser();
		subject.setUsingObject(null);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("object", component.getObject()).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(component.getTemplateUsable().getEndPhrase()), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if (component.getTemplateUsable().userIsInCover()) {
			return 0.3f;
		}
		return 0.0f;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(component.getTemplateUsable().getEndPrompt(), canChoose(subject), new String[]{component.getObject().getName()}, new String[]{component.getTemplateUsable().getEndPrompt().toLowerCase()});
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
