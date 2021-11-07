package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectExit;

public class ActionUnlockExit implements Action {

	private final ObjectExit exit;
	
	public ActionUnlockExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		exit.unlock();
		Context context = new Context(subject, false, exit, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("unlock"), context));
	}

	@Override
	public String getPrompt() {
		return "Unlock " + exit.getFormattedName(false) + " to " + exit.getLinkedArea().getRoom().getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Unlock", exit);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionUnlockExit)) {
            return false;
        } else {
            ActionUnlockExit other = (ActionUnlockExit) o;
            return other.exit == this.exit;
        }
    }
	
}
