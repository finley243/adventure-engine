package personal.finley.adventure_engine_2.action;

import personal.finley.adventure_engine_2.actor.Actor;

public interface IAction {
	
	public void choose(Actor subject);
	
	public String getChoiceName();
	
	public float utility(Actor subject);
	
}
