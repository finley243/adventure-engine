package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.Set;

public class WeaponClass {

    private final String ID;
    private final String name;
    private final boolean isRanged;
    private final boolean isTwoHanded;
    private final boolean isLoud;
    private final Actor.Skill skill;
    private final Set<AreaLink.DistanceCategory> primaryRanges;
    private final Set<String> ammoTypes;
    private final Set<String> attackTypes;

    public WeaponClass(String ID, String name, boolean isRanged, boolean isTwoHanded, boolean isLoud, Actor.Skill skill, Set<AreaLink.DistanceCategory> primaryRanges, Set<String> ammoTypes, Set<String> attackTypes) {
        this.ID = ID;
        this.name = name;
        this.isRanged = isRanged;
        this.isTwoHanded = isTwoHanded;
        this.isLoud = isLoud;
        this.skill = skill;
        this.primaryRanges = primaryRanges;
        this.ammoTypes = ammoTypes;
        this.attackTypes = attackTypes;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public boolean isRanged() {
        return isRanged;
    }

    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public boolean isLoud() {
        return isLoud;
    }

    public Actor.Skill getSkill() {
        return skill;
    }

    public Set<AreaLink.DistanceCategory> getPrimaryRanges() {
        return primaryRanges;
    }

    public Set<String> getAmmoTypes() {
        return ammoTypes;
    }

    public Set<String> getAttackTypes() {
        return attackTypes;
    }

}
