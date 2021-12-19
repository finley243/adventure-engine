package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EffectComponent {

    private final List<Effect> effects;
    private final Actor actor;

    public EffectComponent(Actor actor) {
        effects = new ArrayList<>();
        this.actor = actor;
    }

    public void addEffect(Effect effect) {
        effect.update(actor);
        if(!effect.shouldRemove()) {
            effects.add(effect);
        }
    }

    public void removeEffect(Effect effect) {
        effect.end(actor);
        effects.remove(effect);
    }

    public void onStartTurn() {
        Iterator<Effect> itr = effects.iterator();
        while(itr.hasNext()) {
            Effect effect = itr.next();
            effect.update(actor);
            if(effect.shouldRemove()) {
                itr.remove();
            }
        }
    }

}
