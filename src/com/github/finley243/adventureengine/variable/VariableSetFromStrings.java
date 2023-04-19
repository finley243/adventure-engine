package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VariableSetFromStrings extends Variable {

    private final List<Variable> stringVars;

    public VariableSetFromStrings(List<Variable> stringVars) {
        this.stringVars = stringVars;
    }

    @Override
    public DataType getDataType() {
        return DataType.STRING_SET;
    }

    @Override
    public Set<String> getValueStringSet(ContextScript context) {
        Set<String> stringSet = new HashSet<>();
        for (Variable stringVar : stringVars) {
            stringSet.add(stringVar.getValueString(context));
        }
        return stringSet;
    }

}
