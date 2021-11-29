package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionTalk extends Action {

	private final Actor target;
	
	public ActionTalk(Actor target) {
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject) {
		if(subject instanceof ActorPlayer) {
			((ActorPlayer) subject).startDialogue(target, target.getTopicID());
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && !target.isInCombat();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Talk", "Talk to " + target.getFormattedName(false), canChoose(subject), new String[]{target.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionTalk)) {
            return false;
        } else {
            ActionTalk other = (ActionTalk) o;
            return other.target == this.target;
        }
    }

}
