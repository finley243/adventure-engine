package com.github.finley243.adventureengine.stat;

public interface MutableStatHolder extends StatHolder {

    IntStat getStatInt(String name);

    FloatStat getStatFloat(String name);

    BooleanStat getStatBoolean(String name);

    StringStat getStatString(String name);

    StringSetStat getStatStringSet(String name);

    void onStatChange(String name);

}
