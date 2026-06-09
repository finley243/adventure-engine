package com.github.finley243.adventureengine.actor;

import java.util.Set;

public record SenseType(String ID, String name, Set<String> bypassedObstructionTypes) {}
