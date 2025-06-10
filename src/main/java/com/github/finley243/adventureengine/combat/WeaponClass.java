package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.Set;

public record WeaponClass(String ID, String name, boolean isRanged, boolean isLoud, String skill,
                          Set<AreaLink.DistanceCategory> primaryRanges, Set<String> attackTypes) {

}
