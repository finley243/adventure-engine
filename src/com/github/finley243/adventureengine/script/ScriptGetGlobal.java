package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetGlobal extends Script {

    private final String globalName;

    public ScriptGetGlobal(int line, String globalName) {
        super(line);
        this.globalName = globalName;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression globalValue = context.game().data().getGlobalExpression(globalName);
        return new ScriptReturnData(globalValue, null, null);
    }

}
