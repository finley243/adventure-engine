package com.github.finley243.adventureengine.stat;

public interface MutableStatHolder extends StatHolder {

    StatInt getStatInt(String name);

    StatFloat getStatFloat(String name);

    StatBoolean getStatBoolean(String name);

    StatString getStatString(String name);

    StatStringSet getStatStringSet(String name);

    void onStatChange(String name);

}
