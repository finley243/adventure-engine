package personal.finley.adventure_engine.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import personal.finley.adventure_engine.action.Action;

public class ControllerUtility implements Controller {

	@Override
	public Action chooseAction(Actor actor) {
		List<Action> actions = actor.availableActions();
		List<Action> bestActions = new ArrayList<Action>();
		float maxWeight = 0.0f;
		for(Action currentAction : actions) {
			float currentWeight = currentAction.utility(actor);
			if(currentWeight > maxWeight) {
				maxWeight = currentWeight;
				bestActions.clear();
				bestActions.add(currentAction);
			} else if(currentWeight == maxWeight) {
				bestActions.add(currentAction);
			}
		}
		return bestActions.get(ThreadLocalRandom.current().nextInt(bestActions.size()));
	}

}
