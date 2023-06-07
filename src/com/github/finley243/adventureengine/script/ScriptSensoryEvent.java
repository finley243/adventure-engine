package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScriptSensoryEvent extends Script {

    private final Expression phrase;
    private final Expression phraseAudible;
    private final Expression area;

    public ScriptSensoryEvent(Condition condition, Map<String, Expression> localParameters, Expression phrase, Expression phraseAudible, Expression area) {
        super(condition, localParameters);
        this.phrase = phrase;
        this.phraseAudible = phraseAudible;
        this.area = area;
    }

    @Override
    protected void executeSuccess(Context context) {
        Area[] originAreas = getOriginAreas(context);
        Map<String, String> contextVars = getTextVarMap(context);
        Map<String, Noun> contextNouns = getContextNounMap(context);
        TextContext textContext = new TextContext(contextVars, contextNouns);
        String phraseString = (phrase == null ? null : phrase.getValueString(context));
        String phraseAudibleString = (phraseAudible == null ? null : phraseAudible.getValueString(context));
        context.game().eventBus().post(new SensoryEvent(originAreas, Phrases.get(phraseString), Phrases.get(phraseAudibleString), textContext, false, null, null, context.getSubject(), context.getTarget()));
    }

    private Area[] getOriginAreas(Context context) {
        if (area.getDataType() == Expression.DataType.STRING) {
            String areaID = area.getValueString(context);
            Area[] areaArray = new Area[1];
            areaArray[0] = context.game().data().getArea(areaID);
            return areaArray;
        } else if (area.getDataType() == Expression.DataType.STRING_SET) {
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

    private Map<String, Noun> getContextNounMap(Context context) {
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
        for (Map.Entry<String, Context.Variable> entry : context.getParameters().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.NOUN) {
                nounMap.put(entry.getKey(), entry.getValue().getExpression().getValueNoun(context));
            }
        }
        return nounMap;
    }

    private Map<String, String> getTextVarMap(Context context) {
        Map<String, String> textVarValues = new HashMap<>();
        for (Map.Entry<String, Context.Variable> entry : context.getParameters().entrySet()) {
            if (entry.getValue().getExpression().getDataType() == Expression.DataType.STRING) {
                textVarValues.put(entry.getKey(), entry.getValue().getExpression().getValueString(context));
            }
        }
        return textVarValues;
    }

}
