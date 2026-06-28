package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.world.obstruction.ObstructionType;

import java.util.Set;

public record SenseType(String ID, String name, Set<ObstructionType> bypassedObstructionTypes) {}
