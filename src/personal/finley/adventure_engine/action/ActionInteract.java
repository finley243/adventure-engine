package personal.finley.adventure_engine.action;

import personal.finley.adventure_engine.Game;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.event.TextEvent;
import personal.finley.adventure_engine.event.TextPrintEvent;
import personal.finley.adventure_engine.textgen.Context;
import personal.finley.adventure_engine.textgen.Context.Benefitting;
import personal.finley.adventure_engine.world.INoun;

/*
 * This action opens a sub-menu of actions (for example, a vending machine selection menu or a dialogue with an actor).
 */

public class ActionInteract implements IAction {

	private INoun object;
	private String choice;
	private String text;
	
	public ActionInteract(INoun object, String choice, String text) {
		this.object = object;
		this.choice = choice;
		this.text = text;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, object, object, Benefitting.SUBJECT, false, false);
		TextEvent textEvent = new TextEvent(context, text);
		Game.EVENT_BUS.post(textEvent);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		return choice;
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
