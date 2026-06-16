package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class AmmoItemComponentTemplate extends ItemComponentTemplate {

    private final List<Effect> weaponEffects;
    private final boolean isReusable;

    public AmmoItemComponentTemplate(boolean actionsRestricted, List<Effect> weaponEffects, boolean isReusable) {
        super(actionsRestricted);
        this.weaponEffects = weaponEffects;
        this.isReusable = isReusable;
    }

    public List<Effect> getWeaponEffects() {
        return weaponEffects;
    }

    public boolean isReusable() {
        return isReusable;
    }

}
