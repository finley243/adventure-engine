package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolder;

public class WeaponData implements StatHolder {

    @Override
    public Expression getStatValue(String name, Context context, Game game) {
        return null;
    }

    @Override
    public boolean setStatValue(String name, Expression value, Context context, Game game) {
        return false;
    }

    @Override
    public StatHolder getSubHolder(String name, String ID) {
        return null;
    }

}
