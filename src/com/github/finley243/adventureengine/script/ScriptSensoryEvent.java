package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashSet;
import java.util.Set;

public class ScriptSensoryEvent extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression phrase = context.getLocalVariables().get("phrase").getExpression();
        Expression phraseAudible = context.getLocalVariables().get("phraseAudible").getExpression();
        Expression area = context.getLocalVariables().get("area").getExpression();
        Expression isDetectedBySelfExpression = context.getLocalVariables().get("detectSelf").getExpression();
        if (phrase != null && phrase.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "Phrase parameter is not a string");
        if (phraseAudible != null && phraseAudible.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "PhraseAudible parameter is not a string");
        if (area.getDataType() != Expression.DataType.STRING && area.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Area parameter is not a string or set");
        if (isDetectedBySelfExpression.getDataType() != Expression.DataType.BOOLEAN) return new ScriptReturnData(null, null, "DetectSelf parameter is not a boolean");
        boolean isDetectedBySelf = isDetectedBySelfExpression.getValueBoolean();
        Area[] originAreas = getOriginAreas(context, area);
        String phraseString = (phrase == null ? null : phrase.getValueString());
        String phraseAudibleString = (phraseAudible == null ? null : phraseAudible.getValueString());
        SensoryEvent sensoryEvent = new SensoryEvent(originAreas, Phrases.get(phraseString), Phrases.get(phraseAudibleString), context, isDetectedBySelf, false, context.getParentAction(), null);
        SensoryEvent.execute(context.game(), sensoryEvent);
        return new ScriptReturnData(null, null, null);
    }

    private Area[] getOriginAreas(Context context, Expression area) {
        if (area.getDataType() == Expression.DataType.STRING) {
            String areaID = area.getValueString();
            Area[] areaArray = new Area[1];
            areaArray[0] = context.game().data().getArea(areaID);
            return areaArray;
        } else if (area.getDataType() == Expression.DataType.SET) {
            Set<String> areaIDSet = new HashSet<>();
            for (Expression areaIDExpression : area.getValueSet()) {
                // TODO - Add type checking for set expressions
                areaIDSet.add(areaIDExpression.getValueString());
            }
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
