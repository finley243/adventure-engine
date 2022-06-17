package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.load.SaveData;

import java.util.*;

public class EffectComponent {

    private final Actor actor;
    // Integer value is number of turns effect has been active (if effect does not require manual removal)
    private final Map<Effect, List<Integer>> effects;

    public EffectComponent(Actor actor) {
        effects = new HashMap<>();
        this.actor = actor;
    }

    public void addEffect(Effect effect) {
        if (effect.isInstant()) {
            effect.start(actor);
            effect.end(actor);
        } else {
            if (!effects.containsKey(effect)) {
                effects.put(effect, new ArrayList<>());
            }
            if (effect.isStackable() || !effects.get(effect).isEmpty()) {
                effects.get(effect).add(0);
                effect.start(actor);
            } else {
                effects.get(effect).set(0, 0);
            }
        }
    }

    public void removeEffect(Effect effect) {
        if(effects.containsKey(effect)) {
            effect.end(actor);
            effects.get(effect).remove(0);
            if(effects.get(effect).isEmpty()) {
                effects.remove(effect);
            }
        }
    }

    public void onStartTurn() {
        Iterator<Effect> itr = effects.keySet().iterator();
        while(itr.hasNext()) {
            Effect effect = itr.next();
            effect.eachTurn(actor);
            if(!effect.manualRemoval()) {
                List<Integer> counters = effects.get(effect);
                for (int i = 0; i < counters.size(); i++) {
                    int counterValue = counters.get(i) + 1;
                    counters.set(i, counterValue);
                    if (counterValue == effect.getDuration()) {
                        effect.end(actor);
                        counters.remove(0);
                        if (counters.isEmpty()) {
                            itr.remove();
                        }
                    }
                }
            }
        }
    }

    public List<SaveData> saveState() {
        for(Effect effect : effects.keySet()) {
            if(effect.needsSaveData()) {
                // TODO - Store effect parameters and timer list (only store temporary effects)
            }
        }
        return null;
    }

    public void loadState(List<SaveData> data) {

    }

}
