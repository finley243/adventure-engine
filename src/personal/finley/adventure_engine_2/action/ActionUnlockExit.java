package personal.finley.adventure_engine_2.action;

import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.event.TextEvent;
import personal.finley.adventure_engine_2.event.TextPrintEvent;
import personal.finley.adventure_engine_2.textgen.Context;
import personal.finley.adventure_engine_2.textgen.TextPrinter.Benefitting;
import personal.finley.adventure_engine_2.world.object.ObjectExit;

public class ActionUnlockExit implements IAction {

	private ObjectExit exit;
	
	public ActionUnlockExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		exit.unlock();
		Context context = new Context(subject, exit, exit, Benefitting.SUBJECT, false, false);
		TextEvent text = new TextEvent(context, "unlockExit");
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return "Unlock " + exit.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
