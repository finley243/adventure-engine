package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldActor;

public class ActionTalk implements Action {

	private boolean disabled;
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
	public void disable() {
		disabled = true;
	}

	@Override
	public String getPrompt() {
		return "Talk to " + target.getFormattedName(false);
	}

	@Override
	public float utility(Actor subject) {
		return 0;
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
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldActor("Talk", canChoose(subject), target);
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
