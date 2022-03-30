package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.HashMap;
import java.util.Map;

public class StatComponent {

    public enum ActorStat {
        // ---BASIC---
        HP, MAX_HP,
        // ---ATTRIBUTES---
        BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY,
        // ---SKILLS---
        // BODY
        MELEE, THROWING,
        // INTELLIGENCE
        SOFTWARE, HARDWARE, MEDICINE,
        // CHARISMA
        BARTER, PERSUASION, DECEPTION,
        // DEXTERITY
        HANDGUNS, LONG_ARMS, LOCKPICK,
        // AGILITY
        STEALTH, EVASION
    }

    private final Map<String, Integer> bases;
    private final Map<String, Integer> mods;
    private final Map<String, Float> mults;

    public StatComponent(){
        this.bases = new HashMap<>();
        this.mods = new HashMap<>();
        this.mults = new HashMap<>();
    }

    public int get(String stat) {
        if(!bases.containsKey(stat)) throw new IllegalArgumentException("StatComponent does not contain stat: " + stat);
        int base = bases.get(stat);
        int mod = mods.getOrDefault(stat, 0);
        float mult = mults.getOrDefault(stat, 0.0f);
        return Math.round(base * (mult + 1.0f)) + mod;
    }

    public int getBase(String stat) {
        if(!bases.containsKey(stat)) throw new IllegalArgumentException("StatComponent does not contain stat: " + stat);
        return bases.get(stat);
    }

    public void setBase(String stat, int value) {
        bases.put(stat, value);
    }

    public void addBase(String stat, int value) {
        int currentValue = getBase(stat);
        setBase(stat, currentValue + value);
    }

    public void addMod(String stat, int value) {
        if(!bases.containsKey(stat)) throw new IllegalArgumentException("StatComponent does not contain stat: " + stat);
        int currentValue = mods.getOrDefault(stat, 0);
        mods.put(stat, currentValue + value);
    }

    public void addMult(String stat, float value) {
        if(!bases.containsKey(stat)) throw new IllegalArgumentException("StatComponent does not contain stat: " + stat);
        float currentValue = mults.getOrDefault(stat, 0.0f);
        mults.put(stat, currentValue + value);
    }

}
