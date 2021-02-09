package personal.finley.adventure_engine_2.action.move;

import personal.finley.adventure_engine_2.Game;
import personal.finley.adventure_engine_2.EnumTypes.Benefitting;
import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.event.TextEvent;
import personal.finley.adventure_engine_2.event.TextPrintEvent;
import personal.finley.adventure_engine_2.textgen.Context;
import personal.finley.adventure_engine_2.world.environment.Area;
import personal.finley.adventure_engine_2.world.object.ObjectExit;

public class ActionMoveExit implements IAction {

	private ObjectExit exit;
	
	public ActionMoveExit(ObjectExit exit) {
		this.exit = exit;
	}
	
	@Override
	public void choose(Actor subject) {
		Area area = exit.getLinkedArea();
		subject.move(area);
		Context context = new Context(subject, exit, area.getRoom(), Benefitting.SUBJECT, false, false);
		TextEvent text;
		text = new TextEvent(context, "moveExit");
		/*
		if(area.isProximateName()) {
			text = new TextEvent(context, "moveProx");
		} else {
			text = new TextEvent(context, "move");
		}
		*/
		Game.EVENT_BUS.post(text);
		Game.EVENT_BUS.post(new TextPrintEvent());
	}

	@Override
	public String getChoiceName() {
		//return "Go to " + (area.isProperName() ? "" : "the ") + area.getName();
		return "Go through " + (exit.isProperName() ? "" : "the ") + exit.getName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}

}
