package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public interface StatHolder {

    // TODO - Remove Context (stats don't actually need to use an external context, they can use an internal context)
    Expression getStatValue(String name, Context context);

    /**
     * Sets a static stat with the given name to the given value
     *
     * @param name    the name of the stat
     * @param value   an Expression representing the new value for the stat
     * @param context the Context for evaluating the value expression
     * @return true if the stat is set successfully, false otherwise
     */
    // TODO - Remove Context (stats don't actually need to use an external context, they can use an internal context)
    boolean setStatValue(String name, Expression value, Context context);

    StatHolder getSubHolder(String name, String ID);

}
