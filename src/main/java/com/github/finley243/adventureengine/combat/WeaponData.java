package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.script.ScriptValueHolder;

public class WeaponData implements ScriptValueHolder {

    @Override
    public Expression getScriptValue(String name, Context context) {
        return null;
    }

    @Override
    public boolean setScriptValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public ScriptValueHolder getSubHolder(String name, String ID) {
        return null;
    }

}
