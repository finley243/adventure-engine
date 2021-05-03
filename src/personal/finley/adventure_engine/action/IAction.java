package personal.finley.adventure_engine.action;

import personal.finley.adventure_engine.actor.Actor;

public interface IAction {
	
	public void choose(Actor subject);
	
	public String getChoiceName();
	
	public float utility(Actor subject);
	
}
