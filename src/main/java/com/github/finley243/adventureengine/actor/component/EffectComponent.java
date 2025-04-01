package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

import java.util.*;

public class EffectComponent {

    private final Game game;
    private final MutableStatHolder statHolder;
    private final Context scriptContext;
    private final Map<String, List<EffectData>> effects;

    public EffectComponent(Game game, MutableStatHolder statHolder, Context scriptContext) {
        this.game = game;
        this.statHolder = statHolder;
        this.scriptContext = scriptContext;
        this.effects = new HashMap<>();
    }

    public boolean hasAnyEffect() {
        return !effects.isEmpty();
    }

    public void addEffect(String effectID) {
        Effect effect = game.data().getEffect(effectID);
        if (effect.getConditionAdd() == null || effect.getConditionAdd().isMet(scriptContext)) {
            if (effect.getScriptAdd() != null) {
                effect.getScriptAdd().execute(scriptContext);
            }
            if (effect.isInstant()) {
                effect.start(statHolder);
                effect.end(statHolder);
            } else {
                if (!effects.containsKey(effectID)) {
                    effects.put(effectID, new ArrayList<>());
                }
                if (effect.isStackable() || effects.get(effectID).isEmpty()) {
                    effects.get(effectID).add(new EffectData(effect.getDuration(), true));
                    effect.start(statHolder);
                } else {
                    effects.get(effectID).get(0).turnsRemaining = effect.getDuration();
                }
            }
        }
    }

    public void removeEffect(String effectID) {
        if (effects.containsKey(effectID)) {
            Effect effect = game.data().getEffect(effectID);
            if (effects.get(effectID).get(0).isActive) {
                effect.end(statHolder);
            }
            if (effect.getScriptRemove() != null) {
                effect.getScriptRemove().execute(scriptContext);
            }
            effects.get(effectID).remove(0);
            if (effects.get(effectID).isEmpty()) {
                effects.remove(effectID);
            }
        }
    }

    public void onStartRound() {
        Iterator<String> itr = effects.keySet().iterator();
        while (itr.hasNext()) {
            String effectID = itr.next();
            Effect effect = game.data().getEffect(effectID);
            for (EffectData instance : effects.get(effectID)) {
                if (instance.isActive && effect.getConditionActive() != null && !effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = false;
                    effect.end(statHolder);
                } else if (!instance.isActive && effect.getConditionActive() != null && effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = true;
                    effect.start(statHolder);
                }
                if (effect.getScriptRound() != null) {
                    effect.getScriptRound().execute(scriptContext);
                }
                if (instance.isActive) {
                    effect.eachRound(statHolder);
                }
            }
            if (!effect.manualRemoval()) {
                List<EffectData> effectInstances = effects.get(effectID);
                Iterator<EffectData> instanceItr = effects.get(effectID).iterator();
                while (instanceItr.hasNext()) {
                    EffectData currentInstance = instanceItr.next();
                    if (effect.getDuration() != -1) {
                        currentInstance.turnsRemaining -= 1;
                    }
                    if (currentInstance.turnsRemaining == 0 || (effect.getConditionRemove() != null && effect.getConditionRemove().isMet(scriptContext))) {
                        effect.end(statHolder);
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
