package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.HashSet;
import java.util.Set;

public class WeaponClass {

    private final String ID;
    private final String name;
    private final boolean isRanged;
    private final boolean isLoud;
    private final String skill;
    private final Set<AreaLink.DistanceCategory> primaryRanges;
    private final Set<String> ammoTypes;
    private final Set<String> attackTypes;

    public WeaponClass(String ID, String name, boolean isRanged, boolean isLoud, String skill, Set<AreaLink.DistanceCategory> primaryRanges, Set<String> ammoTypes, Set<String> attackTypes) {
        this.ID = ID;
        this.name = name;
        this.isRanged = isRanged;
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

    public boolean isLoud() {
        return isLoud;
    }

    public String getSkill() {
        return skill;
    }

    public Set<AreaLink.DistanceCategory> getPrimaryRanges() {
        return primaryRanges;
    }

    public Set<String> getPrimaryRangesAsStrings() {
        Set<String> ranges = new HashSet<>();
        for (AreaLink.DistanceCategory distanceCategory : primaryRanges) {
            ranges.add(distanceCategory.toString());
        }
        return ranges;
    }

    public boolean usesAmmo() {
        return !ammoTypes.isEmpty();
    }

    public Set<String> getAmmoTypes() {
        return ammoTypes;
    }

    public Set<String> getAttackTypes() {
        return attackTypes;
    }

}
