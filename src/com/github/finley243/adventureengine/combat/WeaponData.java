package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;

public class WeaponData implements StatHolder {

    @Override
    public Expression getStatValue(String name, Context context) {
        return null;
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

}
