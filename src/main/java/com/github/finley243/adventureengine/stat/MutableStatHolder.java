package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Game;

public interface MutableStatHolder extends StatHolder {

    StatInt getStatInt(Game game, String name);

    StatFloat getStatFloat(Game game, String name);

    StatBoolean getStatBoolean(Game game, String name);

    StatString getStatString(Game game, String name);

    StatStringSet getStatStringSet(Game game, String name);

    void onStatChange(Game game, String name);

}
