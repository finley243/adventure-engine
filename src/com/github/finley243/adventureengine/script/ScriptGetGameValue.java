package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.*;

public class ScriptGetGameValue extends Script {

    private final String valueName;

    public ScriptGetGameValue(int line, String valueName) {
        super(line);
        this.valueName = valueName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        return switch (valueName) {
            case "day" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getDay()), null, null);
            case "month" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getMonth()), null, null);
            case "year" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getYear()), null, null);
            case "weekday" -> new ScriptReturnData(Expression.constant(context.game().data().dateTime().getWeekday()), null, null);
            default -> new ScriptReturnData(null, null, new ScriptErrorData("Specified game value does not exist", -1));
        };
    }

}
