package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataActor;

public class ActionTalk extends Action {

	private final Actor target;
	
	public ActionTalk(Actor subject, ActionDependencies dependencies, Actor target) {
        super(subject, dependencies);
        this.target = target;
	}

	@Override
	public String getID() {
		return "actor_talk";
	}

	@Override
	public Context getContext() {
        return Context.builder().subject(subject).target(target).build();
	}
	
	@Override
	public void choose(int repeatActionCount) {
		target.setKnown();
		game.menuManager().sceneMenu(game, target.getDialogueStart(), Context.builder().subject(target).target(target).build(), true);
	}

	@Override
	public CanChooseResult canChoose() {
		CanChooseResult resultSuper = super.canChoose();
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
	public MenuData getMenuData() {
		return new MenuDataActor(target);
	}

	@Override
	public String getPrompt() {
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
