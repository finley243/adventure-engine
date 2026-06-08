package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

import java.util.*;

public class EffectComponent {

    private final MutableStatHolder statHolder;
    private final Context scriptContext;
    private final Map<Effect, List<EffectData>> effects;

    public EffectComponent(MutableStatHolder statHolder, Context scriptContext) {
        this.statHolder = statHolder;
        this.scriptContext = scriptContext;
        this.effects = new HashMap<>();
    }

    public boolean hasAnyEffect() {
        return !effects.isEmpty();
    }

    public void addEffect(Game game, Effect effect) {
        if (effect.getConditionAdd() == null || effect.getConditionAdd().isMet(scriptContext)) {
            if (effect.getScriptAdd() != null) {
                effect.getScriptAdd().execute(scriptContext);
            }
            if (effect.isInstant()) {
                effect.start(game, statHolder);
                effect.end(game, statHolder);
            } else {
                if (!effects.containsKey(effect)) {
                    effects.put(effect, new ArrayList<>());
                }
                if (effect.isStackable() || effects.get(effect).isEmpty()) {
                    effects.get(effect).add(new EffectData(effect.getDuration(), true));
                    effect.start(game, statHolder);
                } else {
                    effects.get(effect).getFirst().turnsRemaining = effect.getDuration();
                }
            }
        }
    }

    public void removeEffect(Game game, Effect effect) {
        if (effects.containsKey(effect)) {
            if (effects.get(effect).getFirst().isActive) {
                effect.end(game, statHolder);
            }
            if (effect.getScriptRemove() != null) {
                effect.getScriptRemove().execute(scriptContext);
            }
            effects.get(effect).removeFirst();
            if (effects.get(effect).isEmpty()) {
                effects.remove(effect);
            }
        }
    }

    public void onStartRound(Game game) {
        Iterator<Effect> itr = effects.keySet().iterator();
        while (itr.hasNext()) {
            Effect effect = itr.next();
            for (EffectData instance : effects.get(effect)) {
                if (instance.isActive && effect.getConditionActive() != null && !effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = false;
                    effect.end(game, statHolder);
                } else if (!instance.isActive && effect.getConditionActive() != null && effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = true;
                    effect.start(game, statHolder);
                }
                if (effect.getScriptRound() != null) {
                    effect.getScriptRound().execute(scriptContext);
                }
                if (instance.isActive) {
                    effect.eachRound(game, statHolder);
                }
            }
            if (!effect.manualRemoval()) {
                List<EffectData> effectInstances = effects.get(effect);
                Iterator<EffectData> instanceItr = effects.get(effect).iterator();
                while (instanceItr.hasNext()) {
                    EffectData currentInstance = instanceItr.next();
                    if (effect.getDuration() != -1) {
                        currentInstance.turnsRemaining -= 1;
                    }
                    if (currentInstance.turnsRemaining == 0 || (effect.getConditionRemove() != null && effect.getConditionRemove().isMet(scriptContext))) {
                        effect.end(game, statHolder);
                        if (effect.getScriptRemove() != null) {
                            effect.getScriptRemove().execute(scriptContext);
                        }
                        instanceItr.remove();
                        if (effectInstances.isEmpty()) {
                            itr.remove();
                        }
                    }
                }
            }
        }
    }

    /*public List<SaveData> saveState() {
        for (Effect effect : effects.keySet()) {
            if (effect.needsSaveData()) {

            }
        }
        return null;
    }

    public void loadState(List<SaveData> data) {

    }*/

    private static class EffectData {
        public int turnsRemaining;
        public boolean isActive;

        public EffectData(int turnsRemaining, boolean isActive) {
            this.turnsRemaining = turnsRemaining;
            this.isActive = isActive;
        }
    }

}
