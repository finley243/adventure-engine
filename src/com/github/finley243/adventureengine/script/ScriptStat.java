package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptStat extends Script {

    private final StatHolderReference statHolder;
    private final Script statName;

    public ScriptStat(StatHolderReference statHolder, Script statName) {
        this.statHolder = statHolder;
        this.statName = statName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData nameResult = statName.execute(context);
        if (nameResult.error() != null) {
            return nameResult;
        } else if (nameResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        } else if (nameResult.value() == null) {
            return new ScriptReturnData(null, false, false, "Expression did not receive a value");
        } else if (nameResult.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, false, false, "Expression expected a string value");
        }
        String statNameValue = nameResult.value().getValueString();
        Expression statValue = statHolder.getHolder(context).getStatValue(statNameValue, context);
        return new ScriptReturnData(statValue, false, false, null);
    }

}
