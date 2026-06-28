package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.textgen.TextGen;

public record ActionDependencies(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher, TextGen textGen, MenuManager menuManager) {
}
