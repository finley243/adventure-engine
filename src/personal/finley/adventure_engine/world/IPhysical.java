package personal.finley.adventure_engine.world;

import java.util.List;

import personal.finley.adventure_engine.action.IAction;
import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.environment.Area;

public interface IPhysical {
	
	public Area getArea();
	
	// Actions that can be performed within the same area
	public List<IAction> localActions(Actor subject);
	
	// Actions that can be performed anywhere within the same room
	public List<IAction> remoteActions(Actor subject);
	
}
