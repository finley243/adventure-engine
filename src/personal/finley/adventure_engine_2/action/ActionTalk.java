package personal.finley.adventure_engine_2.action;

import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.menu.MenuDialogue;

public class ActionTalk implements IAction {

	private Actor target;
	
	public ActionTalk(Actor target) {
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject) {
		MenuDialogue.buildMenuDialogue(target);
	}

	@Override
	public String getChoiceName() {
		return "Talk to " + target.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0;
	}

}
