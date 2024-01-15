package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.*;

public class ScriptGetGameValue extends Script {

    private final String valueName;

    public ScriptGetGameValue(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        return switch (valueName) {
            case "day" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getDay()), false, false, null);
            case "month" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getMonth()), false, false, null);
            case "year" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getYear()), false, false, null);
            case "weekday" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getWeekday()), false, false, null);
            default -> new ScriptReturnData(null, false, false, "Specified game value does not exist");
        };
    }

}
