package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.Map;
import java.util.Set;

public class ScriptSensoryEvent extends Script {

    /*private final Expression phrase;
    private final Expression phraseAudible;
    private final Expression area;
    private final boolean isDetectedBySelf;

    public ScriptSensoryEvent(Expression phrase, Expression phraseAudible, Expression area, boolean isDetectedBySelf) {
        this.phrase = phrase;
        this.phraseAudible = phraseAudible;
        this.area = area;
        this.isDetectedBySelf = isDetectedBySelf;
    }*/

    @Override
    public ScriptReturnData execute(Context context) {
        Expression phrase = context.getLocalVariables().get("phrase").getExpression();
        Expression phraseAudible = context.getLocalVariables().get("phraseAudible").getExpression();
        Expression area = context.getLocalVariables().get("area").getExpression();
        Expression isDetectedBySelfExpression = context.getLocalVariables().get("detectSelf").getExpression();
        if (phrase != null && phrase.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "Phrase parameter is not a string");
        if (phraseAudible != null && phraseAudible.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "PhraseAudible parameter is not a string");
        if (area.getDataType() != Expression.DataType.STRING && area.getDataType() != Expression.DataType.STRING_SET) return new ScriptReturnData(null, false, false, "Area parameter is not a string or set");
        if (isDetectedBySelfExpression.getDataType() != Expression.DataType.BOOLEAN) return new ScriptReturnData(null, false, false, "DetectSelf parameter is not a boolean");
        boolean isDetectedBySelf = isDetectedBySelfExpression.getValueBoolean();
        Area[] originAreas = getOriginAreas(context, area);
        Map<String, String> contextVars = context.getTextVarMap();
        Map<String, Noun> contextNouns = context.getContextNounMap();
        TextContext textContext = new TextContext(contextVars, contextNouns);
        String phraseString = (phrase == null ? null : phrase.getValueString());
        String phraseAudibleString = (phraseAudible == null ? null : phraseAudible.getValueString());
        SensoryEvent sensoryEvent = new SensoryEvent(originAreas, Phrases.get(phraseString), Phrases.get(phraseAudibleString), textContext, isDetectedBySelf, false, context.getParentAction(), null, context.getSubject(), context.getTarget());
        SensoryEvent.execute(context.game(), sensoryEvent);
        return new ScriptReturnData(null, false, false, null);
    }

    private Area[] getOriginAreas(Context context, Expression area) {
        if (area.getDataType() == Expression.DataType.STRING) {
            String areaID = area.getValueString();
            Area[] areaArray = new Area[1];
            areaArray[0] = context.game().data().getArea(areaID);
            return areaArray;
        } else if (area.getDataType() == Expression.DataType.STRING_SET) {
            Set<String> areaIDSet = area.getValueStringSet();
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

}
