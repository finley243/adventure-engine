package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.*;

public class ScriptGetGameValue extends Script {

    private final String valueName;

    public ScriptGetGameValue(ScriptTraceData traceData, String valueName) {
        super(traceData);
        this.valueName = valueName;
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        return switch (valueName) {
            case "day" -> new ScriptReturnData(Expression.integer(context.game().data().dateTime().getDay()), null, null);
            case "month" -> new ScriptReturnData(Expression.integer(context.game().data().dateTime().getMonth()), null, null);
            case "year" -> new ScriptReturnData(Expression.integer(context.game().data().dateTime().getYear()), null, null);
            case "weekday" -> new ScriptReturnData(Expression.string(context.game().data().dateTime().getWeekday()), null, null);
            default -> new ScriptReturnData(null, null, new ScriptErrorData("Specified game value does not exist", getTraceData()));
        };
    }

}
