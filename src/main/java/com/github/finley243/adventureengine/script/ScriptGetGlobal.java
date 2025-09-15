package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptGetGlobal extends Script {

    private final String globalName;

    public ScriptGetGlobal(ScriptTraceData traceData, String globalName) {
        super(traceData);
        this.globalName = globalName;
    }

    @Override
    public ScriptReturnData execute(Game game, Context context) {
        Expression globalValue = game.data().getGlobalExpression(globalName);
        return new ScriptReturnData(globalValue, null, null);
    }

}
