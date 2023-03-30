package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.MathUtils;

public class VariableRandomStringFromSet extends Variable {

    private final Variable stringSetVar;

    public VariableRandomStringFromSet(Variable stringSetVar) {
        this.stringSetVar = stringSetVar;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING;
    }

    public String getValueString(ContextScript context) {
        return MathUtils.selectRandomFromSet(stringSetVar.getValueStringSet(context));
    }

}
