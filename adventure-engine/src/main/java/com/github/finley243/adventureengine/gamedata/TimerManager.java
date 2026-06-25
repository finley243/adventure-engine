package com.github.finley243.adventureengine.gamedata;

import com.github.finley243.adventureengine.Timer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TimerManager {

    private final Map<String, Timer> timers;

    public TimerManager() {
        this.timers = new HashMap<>();
    }

    public Timer getFromID(String id) {
        return timers.get(id);
    }

    public Collection<Timer> getAll() {
        return timers.values();
    }

    public void add(String id, Timer timer) {
        if (id.trim().isEmpty()) throw new IllegalArgumentException("Cannot add timer with blank ID");
        timers.put(id, timer);
    }

    public void remove(String id) {
        timers.remove(id);
    }

    public boolean isActive(String id) {
        return timers.containsKey(id);
    }

}
