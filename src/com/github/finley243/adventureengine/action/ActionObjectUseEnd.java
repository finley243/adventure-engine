package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
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
		subject.stopUsingObject();
		Context context = new Context(new NounMapper().put("actor", subject).put("object", component.getObject()).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(component.getTemplateUsable().getEndPhrase()), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if(component.getTemplateUsable().userIsInCover()) {
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
        if(!(o instanceof ActionObjectUseEnd)) {
            return false;
        } else {
            ActionObjectUseEnd other = (ActionObjectUseEnd) o;
            return other.component == this.component;
        }
    }

}
