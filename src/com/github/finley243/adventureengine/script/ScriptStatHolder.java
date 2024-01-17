package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptStatHolder extends Script {

    private final StatHolderReference statHolderReference;

    public ScriptStatHolder(StatHolderReference statHolderReference) {
        this.statHolderReference = statHolderReference;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        StatHolder statHolder = statHolderReference.getHolder(context);
        return new ScriptReturnData(Expression.constant(statHolder), null, null);
    }

}
