package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.ScriptEvent;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.*;

public class MutableStatController extends StatController {

    private final Map<String, StatBoolean> booleanStats;
    private final Map<String, StatInt> integerStats;
    private final Map<String, StatFloat> floatStats;
    private final Map<String, StatString> stringStats;
    private final Map<String, StatStringSet> stringSetStats;

    private final Context scriptContext;
    private final Map<String, List<EffectData>> effects;

    public MutableStatController(Game game, Map<String, StatData> dataTypes, Context scriptContext) {
        super(game, dataTypes);
        this.booleanStats = new HashMap<>();
        this.integerStats = new HashMap<>();
        this.floatStats = new HashMap<>();
        this.stringStats = new HashMap<>();
        this.stringSetStats = new HashMap<>();
        this.scriptContext = scriptContext;
        this.effects = new HashMap<>();
    }

    @Override
    public Expression getValue(String name, Context context) {
        if (context == null) context = scriptContext;
        return switch (statData.get(name).dataType()) {
            case BOOLEAN -> (booleanStats.containsKey(name) ? Expression.constant(booleanStats.get(name).value(super.getValue(name, context).getValueBoolean(context), context)) : super.getValue(name, context));
            case INTEGER -> (integerStats.containsKey(name) ? Expression.constant(integerStats.get(name).value(super.getValue(name, context).getValueInteger(context), statData.get(name).minInt(), statData.get(name).maxInt(), context)) : super.getValue(name, context));
            case FLOAT -> (floatStats.containsKey(name) ? Expression.constant(floatStats.get(name).value(super.getValue(name, context).getValueFloat(context), statData.get(name).minFloat(), statData.get(name).maxFloat(), context)) : super.getValue(name, context));
            case STRING -> (stringStats.containsKey(name) ? Expression.constant(stringStats.get(name).value(super.getValue(name, context).getValueString(context), context)) : super.getValue(name, context));
            case STRING_SET -> (stringSetStats.containsKey(name) ? Expression.constant(stringSetStats.get(name).value(super.getValue(name, context).getValueStringSet(context), context)) : super.getValue(name, context));
            case null, default -> null;
        };
    }

    public StatBoolean getStatBoolean(String name) {
        return booleanStats.get(name);
    }

    public StatInt getStatInteger(String name) {
        return integerStats.get(name);
    }

    public StatFloat getStatFloat(String name) {
        return floatStats.get(name);
    }

    public StatString getStatString(String name) {
        return stringStats.get(name);
    }

    public StatStringSet getStatStringSet(String name) {
        return stringSetStats.get(name);
    }

    public void addEffect(String effectID) {
        Effect effect = game.data().getEffect(effectID);
        if (effect.getConditionAdd() == null || effect.getConditionAdd().isMet(scriptContext)) {
            if (effect.getScriptAdd() != null) {
                game.eventQueue().addToEnd(new ScriptEvent(effect.getScriptAdd(), scriptContext));
            }
            if (effect.isInstant()) {
                effect.start(this);
                effect.end(this);
            } else {
                if (!effects.containsKey(effectID)) {
                    effects.put(effectID, new ArrayList<>());
                }
                if (effect.isStackable() || effects.get(effectID).isEmpty()) {
                    effects.get(effectID).add(new EffectData(effect.getDuration(), true));
                    effect.start(this);
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
                effect.end(this);
            }
            if (effect.getScriptRemove() != null) {
                game.eventQueue().addToEnd(new ScriptEvent(effect.getScriptRemove(), scriptContext));
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
                    effect.end(this);
                } else if (!instance.isActive && effect.getConditionActive() != null && effect.getConditionActive().isMet(scriptContext)) {
                    instance.isActive = true;
                    effect.start(this);
                }
                if (effect.getScriptRound() != null) {
                    game.eventQueue().addToEnd(new ScriptEvent(effect.getScriptRound(), scriptContext));
                }
                if (instance.isActive) {
                    effect.eachRound(this);
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
                        effect.end(this);
                        if (effect.getScriptRemove() != null) {
                            game.eventQueue().addToEnd(new ScriptEvent(effect.getScriptRemove(), scriptContext));
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
