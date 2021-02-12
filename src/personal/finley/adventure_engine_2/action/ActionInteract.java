package personal.finley.adventure_engine_2.action;

import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.event.TextEvent;
import personal.finley.adventure_engine_2.event.TextPrintEvent;
import personal.finley.adventure_engine_2.textgen.Context;
import personal.finley.adventure_engine_2.textgen.Context.Benefitting;
import personal.finley.adventure_engine_2.world.INoun;

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
