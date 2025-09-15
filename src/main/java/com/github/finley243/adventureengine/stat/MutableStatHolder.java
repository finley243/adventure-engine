package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Game;

public interface MutableStatHolder extends StatHolder {

    StatInt getStatInt(String name, Game game);

    StatFloat getStatFloat(String name, Game game);

    StatBoolean getStatBoolean(String name, Game game);

    StatString getStatString(String name, Game game);

    StatStringSet getStatStringSet(String name, Game game);

    void onStatChange(String name, Game game);

}
