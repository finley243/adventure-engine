package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ActionComponentUtility implements ActionComponent {

    private final Actor actor;

    public ActionComponentUtility(Actor actor) {
        this.actor = actor;
    }

    @Override
    public Action chooseAction(List<Action> actions) {
        int chaos = 1;
        List<List<Action>> bestActions = new ArrayList<>(chaos + 1);
        List<Float> maxWeights = new ArrayList<>(chaos + 1);
        for(int i = 0; i < chaos + 1; i++) {
            bestActions.add(new ArrayList<>());
            maxWeights.add(0.0f);
        }
        for(Action currentAction : actions) {
            if(currentAction.canChoose(actor)) {
                float currentWeight = currentAction.utility(actor);
                float behaviorOverride = actor.behaviorComponent().actionUtilityOverride(currentAction);
                if(behaviorOverride >= 0.0f) {
                    currentWeight = behaviorOverride;
                }
                if(currentWeight != 0) {
                    for (int i = 0; i < chaos + 1; i++) {
                        if (currentWeight == maxWeights.get(i)) {
                            bestActions.get(i).add(currentAction);
                            break;
                        } else if (currentWeight > maxWeights.get(i)) {
                            maxWeights.remove(maxWeights.size() - 1);
                            maxWeights.add(i, currentWeight);
                            bestActions.remove(bestActions.size() - 1);
                            bestActions.add(i, new ArrayList<>());
                            bestActions.get(i).add(currentAction);
                            break;
                        }
                    }
                }
            }
        }
        float weightSum = 0.0f;
        for(float weight : maxWeights) {
            weightSum += weight;
        }
        float partialWeightSum = 0.0f;
        float random = ThreadLocalRandom.current().nextFloat();
        for(int i = 0; i < chaos + 1; i++) {
            if(random < partialWeightSum + (maxWeights.get(i) / weightSum)) {
                return bestActions.get(i).get(ThreadLocalRandom.current().nextInt(bestActions.get(i).size()));
            } else {
                partialWeightSum += (maxWeights.get(i) / weightSum);
            }
        }
        return null;
    }

}
