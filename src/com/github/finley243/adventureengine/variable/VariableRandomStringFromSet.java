package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.MathUtils;

import java.util.Set;

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
        Set<String> set = stringSetVar.getValueStringSet(context);
        if (set.isEmpty()) throw new UnsupportedOperationException("Provided string set is empty");
        return MathUtils.selectRandomFromSet(set);
    }

}
