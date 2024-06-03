package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.Context;

@FunctionalInterface
public interface TextProcessor {

    String process(String line, Context context, String originalLine);

}
