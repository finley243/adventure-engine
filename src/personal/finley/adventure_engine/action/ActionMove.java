package personal.finley.adventure_engine.action;

import personal.finley.adventure_engine.Game;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.event.TextEvent;
import personal.finley.adventure_engine.event.TextPrintEvent;
import personal.finley.adventure_engine.textgen.Context;
import personal.finley.adventure_engine.textgen.Context.Benefitting;
import personal.finley.adventure_engine.world.environment.Area;

public class ActionMove implements IAction {
	
	private Area area;
	
	public ActionMove(Area area) {
		this.area = area;
	}
	
	@Override
	public void choose(Actor subject) {
		subject.move(area);
		Context context = new Context(subject, area, area, Benefitting.SUBJECT, false, false);
		TextEvent text;
		if(area.isProximateName()) {
			text = new TextEvent(context, "moveProx");
		} else {
			text = new TextEvent(context, "move");
		}
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}
	
	@Override
	public String getChoiceName() {
		return "Go toward " + area.getFormattedName();
	}
	
	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
