package personal.finley.adventure_engine_2.world.object;

import java.util.ArrayList;
import java.util.List;

import personal.finley.adventure_engine_2.action.IAction;
import personal.finley.adventure_engine_2.action.interact.ActionInteract;
import personal.finley.adventure_engine_2.actor.Actor;

public class ObjectInteractGeneric extends ObjectBase {

	private String verb;
	private String useText;
	
	public ObjectInteractGeneric(String ID, String name, String verb, String useText) {
		super(ID, name);
		this.verb = verb;
		this.useText = useText;
	}

	@Override
	public List<IAction> localActions(Actor subject) {
		List<IAction> actions = new ArrayList<IAction>();
		actions.add(new ActionInteract(this, verb + " " + getName(), useText));
		return actions;
	}
	
}
