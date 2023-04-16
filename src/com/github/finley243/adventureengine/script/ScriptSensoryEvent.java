package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.variable.Variable;
import com.github.finley243.adventureengine.variable.VariableLiteral;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScriptSensoryEvent extends Script {

    private final String phrase;
    private final String phraseAudible;
    private final Variable area;

    public ScriptSensoryEvent(Condition condition, Map<String, Variable> localParameters, String phrase, String phraseAudible, Variable area) {
        super(condition, localParameters);
        this.phrase = phrase;
        this.phraseAudible = phraseAudible;
        this.area = area;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        Area[] originAreas = getOriginAreas(context);
        Map<String, String> contextVars = getTextVarMap(context);
        Map<String, Noun> contextNouns = getContextNounMap(context);
        Context textContext = new Context(contextVars, contextNouns);
        context.game().eventBus().post(new SensoryEvent(originAreas, Phrases.get(phrase), Phrases.get(phraseAudible), textContext, false, null, null, context.getSubject(), context.getTarget()));
    }

    private Area[] getOriginAreas(ContextScript context) {
        if (area.getDataType() == Variable.DataType.STRING) {
            String areaID = area.getValueString(context);
            Area[] areaArray = new Area[1];
            areaArray[0] = context.game().data().getArea(areaID);
            return areaArray;
        } else if (area.getDataType() == Variable.DataType.STRING_SET) {
            Set<String> areaIDSet = area.getValueStringSet(context);
            Area[] areaArray = new Area[areaIDSet.size()];
            int index = 0;
            for (String areaID : areaIDSet) {
                areaArray[index] = context.game().data().getArea(areaID);
                index++;
            }
            return areaArray;
        }
        return null;
    }

    private Map<String, Noun> getContextNounMap(ContextScript context) {
        Map<String, Noun> nounMap = new HashMap<>();
        if (context.getSubject() != null) {
            nounMap.put("actor", context.getSubject());
        }
        if (context.getTarget() != null) {
            nounMap.put("target", context.getTarget());
        }
        if (context.getParentObject() != null) {
            nounMap.put("object", context.getParentObject());
        }
        if (context.getParentItem() != null) {
            nounMap.put("item", context.getParentItem());
        }
        for (Map.Entry<String, Variable> entry : context.getParameters().entrySet()) {
            if (entry.getValue().getDataType() == Variable.DataType.NOUN) {
                nounMap.put(entry.getKey(), entry.getValue().getValueNoun(context));
            }
        }
        return nounMap;
    }

    private Map<String, String> getTextVarMap(ContextScript context) {
        Map<String, String> textVarValues = new HashMap<>();
        for (Map.Entry<String, Variable> entry : context.getParameters().entrySet()) {
            if (entry.getValue().getDataType() == Variable.DataType.STRING) {
                textVarValues.put(entry.getKey(), entry.getValue().getValueString(context));
            }
        }
        return textVarValues;
    }

}
