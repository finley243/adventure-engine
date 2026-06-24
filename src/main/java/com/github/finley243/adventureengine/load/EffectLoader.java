package com.github.finley243.adventureengine.load;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.effect.*;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectLoader {

    private static final String NAME_EFFECT = "effect";

    private final ScriptPipeline scriptPipeline;
    private final ScriptRuntime scriptRuntime;
    private final Set<String> knownFunctions;

    public EffectLoader(ScriptPipeline scriptPipeline, ScriptRuntime scriptRuntime, Set<String> knownFunctions) {
        this.scriptPipeline = scriptPipeline;
        this.scriptRuntime = scriptRuntime;
        this.knownFunctions = knownFunctions;
    }

    public Map<String, Effect> load(Element element) {
        return LoadUtils.loadAll(element, NAME_EFFECT, this::parseEffect, Effect::getID);
    }

    private Effect parseEffect(Element element) {
        if (element == null) return null;
        String ID = LoadUtils.attribute(element, "id", null);
        boolean manualRemoval = LoadUtils.attributeBool(element, "permanent", false);
        String effectType = LoadUtils.attribute(element, "type", null);
        int duration = LoadUtils.attributeInt(element, "duration", 0);
        boolean stackable = LoadUtils.attributeBool(element, "stack", true);
        Condition conditionAdd = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "conditionAdd"), scriptPipeline, "Effect(" + ID + ") - add condition", scriptRuntime , knownFunctions);
        Condition conditionRemove = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "conditionRemove"), scriptPipeline, "Effect(" + ID + ") - remove condition", scriptRuntime, knownFunctions);
        Condition conditionActive = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "conditionActive"), scriptPipeline, "Effect(" + ID + ") - active condition", scriptRuntime, knownFunctions);
        Script scriptAdd = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, "scriptAdd"), scriptPipeline, "Effect(" + ID + ") - add script", knownFunctions);
        Script scriptRemove = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, "scriptRemove"), scriptPipeline, "Effect(" + ID + ") - remove script", knownFunctions);
        Script scriptRound = LoadUtils.loadScript(LoadUtils.singleChildWithName(element, "scriptRound"), scriptPipeline, "Effect(" + ID + ") - round script", knownFunctions);
        switch (effectType) {
            case "add" -> {
                String statMod = LoadUtils.attribute(element, "stat", null);
                String statModValue = LoadUtils.attribute(element, "amount", "0");
                Condition statCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "statCondition"), scriptPipeline, "Effect(" + ID + ") - stat condition", scriptRuntime, knownFunctions);
                boolean statModIsFloat = statModValue.contains(".");
                if (statModIsFloat) {
                    float statModValueFloat = Float.parseFloat(statModValue);
                    return new AddFloatEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMod, statModValueFloat, statCondition);
                } else {
                    int statModValueInt = Integer.parseInt(statModValue);
                    return new AddIntEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMod, statModValueInt, statCondition);
                }
            }
            case "mult" -> {
                String statMult = LoadUtils.attribute(element, "stat", null);
                float statMultAmount = LoadUtils.attributeFloat(element, "amount", 0.0f);
                Condition statCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "statCondition"), scriptPipeline, "Effect(" + ID + ") - stat condition", scriptRuntime, knownFunctions);
                return new MultEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statMult, statMultAmount, statCondition);
            }
            case "boolean" -> {
                String statBoolean = LoadUtils.attribute(element, "stat", null);
                boolean statBooleanValue = LoadUtils.attributeBool(element, "value", true);
                Condition statCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "statCondition"), scriptPipeline, "Effect(" + ID + ") - stat condition", scriptRuntime, knownFunctions);
                return new BooleanEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statBoolean, statBooleanValue, statCondition);
            }
            case "string" -> {
                String statString = LoadUtils.attribute(element, "stat", null);
                String statStringValue = LoadUtils.attribute(element, "value", null);
                Condition statCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "statCondition"), scriptPipeline, "Effect(" + ID + ") - stat condition", scriptRuntime, knownFunctions);
                return new StringEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statString, statStringValue, statCondition);
            }
            case "stringSet" -> {
                String statStringSet = LoadUtils.attribute(element, "stat", null);
                Set<String> stringSetValuesAdd = LoadUtils.setOfTags(element, "add");
                Set<String> stringSetValuesRemove = LoadUtils.setOfTags(element, "remove");
                Condition statCondition = LoadUtils.loadCondition(LoadUtils.singleChildWithName(element, "statCondition"), scriptPipeline, "Effect(" + ID + ") - stat condition", scriptRuntime, knownFunctions);
                return new StringSetEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, statStringSet, stringSetValuesAdd, stringSetValuesRemove, statCondition);
            }
            case "compound" -> {
                List<Effect> compoundEffects = parseEffects(element);
                return new CompoundEffect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound, compoundEffects);
            }
            case null -> { // "basic"
                return new Effect(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
            }
            default -> throw new GameDataException("Effect has invalid type: " + effectType);
        }
    }

    private List<Effect> parseEffects(Element effectsElement) {
        if (effectsElement == null) return new ArrayList<>();
        List<Element> effectElements = LoadUtils.directChildrenWithName(effectsElement, "effect");
        List<Effect> effects = new ArrayList<>();
        for (Element effectElement : effectElements) {
            effects.add(parseEffect(effectElement));
        }
        return effects;
    }

}
