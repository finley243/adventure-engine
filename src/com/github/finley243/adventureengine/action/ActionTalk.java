package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;

public class ActionTalk extends Action {

	private final Actor target;
	
	public ActionTalk(Actor target) {
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.game().menuManager().sceneMenu(target, target, target.getDialogueStart());
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && subject.isPlayer() && !target.isInCombat() && target.getDialogueStart().canChoose(target, target);
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Talk", canChoose(subject), new String[]{LangUtils.titleCase(target.getName())}, new String[]{"talk to " + target.getName(), "talk " + target.getName(), "talk with " + target.getName(), "speak to " + target.getName(), "speak with " + target.getName()});
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
