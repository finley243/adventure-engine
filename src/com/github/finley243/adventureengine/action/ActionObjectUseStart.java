package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
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
		if (component.getTemplateUsable().userIsInCover()) {
			subject.triggerScript("on_take_cover", subject);
		}
		component.setUser(subject);
		subject.startUsingObject(component);
		Context context = new Context(new MapBuilder<String, Noun>().put("actor", subject).put("object", component.getObject()).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(component.getTemplateUsable().getStartPhrase()), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if(component.getTemplateUsable().userIsInCover()) {
			return UtilityUtils.getCoverUtility(subject);
		}
		return 0.0f;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(component.getTemplateUsable().getStartPrompt(), canChoose(subject), new String[]{component.getObject().getName()}, new String[]{component.getTemplateUsable().getStartPrompt().toLowerCase()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionObjectUseStart)) {
            return false;
        } else {
            ActionObjectUseStart other = (ActionObjectUseStart) o;
            return other.component == this.component;
        }
    }

}
