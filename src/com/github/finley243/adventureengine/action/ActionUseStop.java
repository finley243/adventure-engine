package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.UsableObject;

public class ActionUseStop extends Action {

	private final UsableObject object;
	
	public ActionUseStop(UsableObject object) {
		this.object = object;
	}
	
	@Override
	public void choose(Actor subject) {
		object.removeUser();
		subject.stopUsingObject();
		Context context = new Context(subject, object);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get(object.getStopPhrase()), context, this, subject));
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
