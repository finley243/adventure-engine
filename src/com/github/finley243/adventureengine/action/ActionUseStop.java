package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionUseStop extends Action {

	private final UsableObject object;
	
	public ActionUseStop(UsableObject object) {
		this.object = object;
	}

	public UsableObject getObject() {
		return object;
	}
	
	@Override
	public void choose(Actor subject) {
		if (object.userInCover()) {
			subject.triggerScript("on_leave_cover");
		}
		object.removeUser();
		subject.stopUsingObject();
		Context context = new Context(new NounMapper().put("actor", subject).put("object", object).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(object.getStopPhrase()), context, this, subject));
	}

	@Override
	public float utility(Actor subject) {
		if(object.userInCover()) {
			return 0.3f;
		}
		return 0.0f;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(object.getStopPrompt(), canChoose(subject), new String[]{object.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUseStop)) {
            return false;
        } else {
            ActionUseStop other = (ActionUseStop) o;
            return other.object == this.object;
        }
    }

}
