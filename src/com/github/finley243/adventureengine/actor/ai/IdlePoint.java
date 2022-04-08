package com.github.finley243.adventureengine.actor.ai;

public class IdlePoint {

    private final String area;
    private final int duration;
    private final String idlePhrase;

    public IdlePoint(String area, int duration, String idlePhrase) {
        this.area = area;
        this.duration = duration;
        this.idlePhrase = idlePhrase;
    }

    public String getArea() {
        return area;
    }

    public int getDuration() {
        return duration;
    }

    public String getIdlePhrase() {
        return idlePhrase;
    }

}
