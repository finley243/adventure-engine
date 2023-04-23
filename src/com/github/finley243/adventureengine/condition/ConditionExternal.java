package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;

public class ConditionExternal extends Condition {

    private final String conditionID;

    public ConditionExternal(boolean invert, String conditionID) {
        super(invert);
        this.conditionID = conditionID;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        return context.game().data().getCondition(conditionID).isMet(context);
    }

}
