package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;

public class ActionTalk extends Action {

	private final Actor target;
	
	public ActionTalk(Actor target) {
		this.target = target;
	}

	@Override
	public String getID() {
		return "actor_talk";
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.game().menuManager().sceneMenu(subject.game(), target.getDialogueStart(), null, new Context(target.game(), target, target));
	}

	@Override
	public CanChooseResult canChoose(Actor subject) {
		CanChooseResult resultSuper = super.canChoose(subject);
		if (!resultSuper.canChoose()) {
			return resultSuper;
		}
		if (!subject.isPlayer()) {
			return new CanChooseResult(false, "Player only");
		}
		if (target.isInCombat()) {
			return new CanChooseResult(false, "Target is in combat");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataActor(target);
	}

	@Override
	public String getPrompt(Actor subject) {
		return "Talk";
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionTalk other)) {
            return false;
        } else {
			return other.target == this.target;
        }
    }

	@Override
	public ActionDetectionChance detectionChance() {
		return ActionDetectionChance.HIGH;
	}

}
