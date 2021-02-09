package personal.finley.adventure_engine_2.actor;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import personal.finley.adventure_engine_2.action.IAction;

public class ControllerRandom implements IController {
	
	public ControllerRandom() {}

	@Override
	public IAction chooseAction(Actor actor) {
		List<IAction> actions = actor.availableActions();
		int index = ThreadLocalRandom.current().nextInt(actions.size());
		return actions.get(index);
	}
	
}
