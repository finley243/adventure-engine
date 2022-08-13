package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionTalk extends Action {

	private final Actor target;
	
	public ActionTalk(Actor target) {
		super(ActionDetectionChance.HIGH);
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
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Talk", canChoose(subject), new String[]{target.getName()});
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
