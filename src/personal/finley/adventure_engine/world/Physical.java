package personal.finley.adventure_engine.world;

import java.util.List;

import personal.finley.adventure_engine.action.Action;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.environment.Area;

public interface Physical {
	
	public Area getArea();
	
	// Actions that can be performed within the same area
	public List<Action> localActions(Actor subject);
	
	// Actions that can be performed anywhere within the same room
	public List<Action> remoteActions(Actor subject);
	
}
