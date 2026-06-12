package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

import java.util.*;

public class EffectComponent {

    private final MutableStatHolder statHolder;
    private final ScriptRuntime scriptRuntime;
    private final Context scriptContext;
    private final Map<Effect, List<EffectData>> effects;

    public EffectComponent(MutableStatHolder statHolder, ScriptRuntime scriptRuntime, Context scriptContext) {
        this.statHolder = statHolder;
        this.scriptRuntime = scriptRuntime;
        this.scriptContext = scriptContext;
        this.effects = new HashMap<>();
    }

    public boolean hasAnyEffect() {
        return !effects.isEmpty();
    }

    public void addEffect(Effect effect) {
        if (effect.getConditionAdd() == null || effect.getConditionAdd().isMet(scriptContext)) {
            if (effect.getScriptAdd() != null) {
                effect.getScriptAdd().run(scriptRuntime, scriptContext);
            }
            if (effect.isInstant()) {
                effect.start(statHolder);
                effect.end(statHolder);
            } else {
                if (!effects.containsKey(effect)) {
                    effects.put(effect, new ArrayList<>());
                }
                if (effect.isStackable() || effects.get(effect).isEmpty()) {
                    effects.get(effect).add(new EffectData(effect.getDuration(), true));
                    effect.start(statHolder);
                } else {
                    effects.get(effect).getFirst().turnsRemaining = effect.getDuration();
                }
            }
        }
    }

    public void removeEffect(Effect effect) {
        if (effects.containsKey(effect)) {
            if (effects.get(effect).getFirst().isActive) {
                effect.end(statHolder);
            }
            if (effect.getScriptRemove() != null) {
                effect.getScriptRemove().run(scriptRuntime, scriptContext);
            }
            effects.get(effect).removeFirst();
            if (effects.get(effect).isEmpty()) {
                effects.remove(effect);
            }
        }
    }

    public void onStartRound() {
        Iterator<Effect> itr = effects.keySet().iterator();
        while (itr.hasNext()) {
            Effect effect = itr.next();
            for (EffectData instance : effects.get(effect)) {
                if (instance.isActive && effect.getConditionActive() != null && !effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = false;
                    effect.end(statHolder);
                } else if (!instance.isActive && effect.getConditionActive() != null && effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = true;
                    effect.start(statHolder);
                }
                if (effect.getScriptRound() != null) {
                    effect.getScriptRound().run(scriptRuntime, scriptContext);
                }
                if (instance.isActive) {
                    effect.eachRound(statHolder);
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
                        effect.end(statHolder);
                        if (effect.getScriptRemove() != null) {
                            effect.getScriptRemove().run(scriptRuntime, scriptContext);
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

    private static class EffectData {
        public int turnsRemaining;
        public boolean isActive;

        public EffectData(int turnsRemaining, boolean isActive) {
            this.turnsRemaining = turnsRemaining;
            this.isActive = isActive;
        }
    }

}
