package personal.finley.adventure_engine_2.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import personal.finley.adventure_engine_2.action.IAction;

public class ControllerUtility implements IController {

	@Override
	public IAction chooseAction(Actor actor) {
		List<IAction> actions = actor.availableActions();
		List<IAction> bestActions = new ArrayList<IAction>();
		float maxWeight = 0.0f;
		for(IAction currentAction : actions) {
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
