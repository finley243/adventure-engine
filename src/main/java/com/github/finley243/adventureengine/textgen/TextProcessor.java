package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;

@FunctionalInterface
public interface TextProcessor {

    String process(String line, Game game, Context context, String originalLine);

}
