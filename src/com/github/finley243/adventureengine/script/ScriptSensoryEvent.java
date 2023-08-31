package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.Map;
import java.util.Set;

public class ScriptSensoryEvent extends Script {

    private final Expression phrase;
    private final Expression phraseAudible;
    private final Expression area;

    public ScriptSensoryEvent(Condition condition, Expression phrase, Expression phraseAudible, Expression area) {
        super(condition);
        this.phrase = phrase;
        this.phraseAudible = phraseAudible;
        this.area = area;
    }

    @Override
    protected void executeSuccess(Context context) {
        Area[] originAreas = getOriginAreas(context);
        Map<String, String> contextVars = context.getTextVarMap();
        Map<String, Noun> contextNouns = context.getContextNounMap();
        TextContext textContext = new TextContext(contextVars, contextNouns);
        String phraseString = (phrase == null ? null : phrase.getValueString(context));
        String phraseAudibleString = (phraseAudible == null ? null : phraseAudible.getValueString(context));
        context.game().eventQueue().addToFront(new SensoryEvent(originAreas, Phrases.get(phraseString), Phrases.get(phraseAudibleString), textContext, false, null, null, context.getSubject(), context.getTarget()));
        context.game().eventQueue().executeNext();
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

}
