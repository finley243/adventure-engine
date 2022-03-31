package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.*;

public class EffectComponent {

    private final Actor actor;
    // Integer value is number of turns effect has been active (if effect does not require manual removal)
    private final Map<Effect, Integer> effects;

    public EffectComponent(Actor actor) {
        effects = new HashMap<>();
        this.actor = actor;
    }

    public void addEffect(Effect effect) {
        effect.start(actor);
        if(!effect.isInstant()) {
            effects.put(effect, 0);
        }
    }

    public void removeEffect(Effect effect) {
        if(effects.containsKey(effect)) {
            effect.end(actor);
            effects.remove(effect);
        }
    }

    public void onStartTurn() {
        Iterator<Effect> itr = effects.keySet().iterator();
        while(itr.hasNext()) {
            Effect effect = itr.next();
            effect.eachTurn(actor);
            int counterValue = effects.get(effect) + 1;
            effects.put(effect, counterValue);
            if(!effect.manualRemoval() && counterValue == effect.getDuration()) {
                effect.end(actor);
                itr.remove();
            }
        }
    }

}
