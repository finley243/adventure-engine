package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.actor.Actor;

public class MathUtils {

    private static final float SIGMOID_SCALE = 3.0f;

    public static float chanceSigmoid(int value, int min, int max) {
        int centeredValue = value - ((max - min + 1) / 2);
        float normalizedValue = ((float) centeredValue) / ((float) (max - min));
        return (1.0f / (1.0f + (float) Math.exp(-normalizedValue * SIGMOID_SCALE)));
    }

    public static float chanceLinear(int value, int valueMin, int valueMax, float chanceMin, float chanceMax) {
        return chanceMin + ((chanceMax - chanceMin) / (valueMax - valueMin)) * (value - valueMin);
    }

}
