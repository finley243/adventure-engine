package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionExitUnlock extends Action {

	private final ObjectExit exit;
	
	public ActionExitUnlock(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		exit.unlock();
		Context context = new Context(subject, exit);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("unlock"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		if(disabled || !exit.isLocked()) return false;
		boolean hasKey = false;
		for(String keyID : exit.getKeyIDs()) {
			if(subject.inventory().hasItemWithID(keyID)) {
				hasKey = true;
				break;
			}
		}
		return hasKey;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Unlock", canChoose(subject), new String[]{exit.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionExitUnlock)) {
            return false;
        } else {
            ActionExitUnlock other = (ActionExitUnlock) o;
            return other.exit == this.exit;
        }
    }
	
}
