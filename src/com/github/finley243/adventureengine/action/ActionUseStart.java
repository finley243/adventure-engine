package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionUseStart extends Action {

	private final UsableObject object;
	
	public ActionUseStart(UsableObject object) {
		super(ActionDetectionChance.LOW);
		this.object = object;
	}

	public UsableObject getObject() {
		return object;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		if (object.userInCover()) {
			subject.triggerScript("on_take_cover", subject);
		}
		object.setUser(subject);
		subject.startUsingObject(object);
		Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(object.getStartPhrase()), context, this, null, subject, null));
	}

	@Override
	public float utility(Actor subject) {
		if(object.userInCover()) {
			return UtilityUtils.getCoverUtility(subject);
		}
		return 0.0f;
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(object.getStartPrompt(), canChoose(subject), new String[]{object.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUseStart)) {
            return false;
        } else {
            ActionUseStart other = (ActionUseStart) o;
            return other.object == this.object;
        }
    }

}
