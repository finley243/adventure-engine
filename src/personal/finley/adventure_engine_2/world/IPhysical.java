package personal.finley.adventure_engine_2.world;

import java.util.List;

import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.actor.Actor;
import personal.finley.adventure_engine_2.world.environment.Area;

public interface IPhysical {
	
	public Area getArea();
	
	// Actions that can be performed within the same area
	public List<IAction> localActions(Actor subject);
	
	// Actions that can be performed anywhere within the same room
	public List<IAction> remoteActions(Actor subject);
	
}
