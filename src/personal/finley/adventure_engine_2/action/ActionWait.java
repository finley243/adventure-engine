package personal.finley.adventure_engine_2.action;

import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.event.TextEvent;
import personal.finley.adventure_engine_2.event.TextPrintEvent;
import personal.finley.adventure_engine_2.textgen.Context;
import personal.finley.adventure_engine_2.textgen.Context.Benefitting;

public class ActionWait implements IAction {

	public ActionWait() {
		
	}

	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, Benefitting.SUBJECT, false, false);
		TextEvent text = new TextEvent(context, "wait");
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return "Wait";
	}

	@Override
	public float utility(Actor subject) {
		return 0.00001f;
	}
	
}
