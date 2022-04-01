package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;

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
        effect.start(actor);
        if(!effect.isInstant()) {
            if(!effects.containsKey(effect)) {
                effects.put(effect, new ArrayList<>());
            }
            effects.get(effect).add(0);
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
            List<Integer> counters = effects.get(effect);
            for(int i = 0; i < counters.size(); i++) {
                int counterValue = counters.get(i) + 1;
                counters.set(i, counterValue);
                if(!effect.manualRemoval() && counterValue == effect.getDuration()) {
                    effect.end(actor);
                    counters.remove(0);
                    if(counters.isEmpty()) {
                        itr.remove();
                    }
                }
            }
        }
    }

}
