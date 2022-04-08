package com.github.finley243.adventureengine.actor.ai;

public class BehaviorTarget {

    public enum TargetType {
        AREA, OBJECT
    }

    private final TargetType type;
    private final String target;
    private final int duration;
    private final String idlePhraseStart;
    private final String idlePhraseEnd;

    public BehaviorTarget(TargetType type, String target, int duration, String idlePhraseStart, String idlePhraseEnd) {
        this.type = type;
        this.target = target;
        this.duration = duration;
        this.idlePhraseStart = idlePhraseStart;
        this.idlePhraseEnd = idlePhraseEnd;
    }

    public TargetType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public int getDuration() {
        return duration;
    }

    public String getIdlePhraseStart() {
        return idlePhraseStart;
    }

    public String getIdlePhraseEnd() {
        return idlePhraseEnd;
    }

}
