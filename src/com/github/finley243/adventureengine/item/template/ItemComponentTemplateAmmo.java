package com.github.finley243.adventureengine.item.template;

import java.util.List;

public class ItemComponentTemplateAmmo extends ItemComponentTemplate {

    private final List<String> weaponEffects;
    private final boolean isReusable;

    public ItemComponentTemplateAmmo(boolean actionsRestricted, List<String> weaponEffects, boolean isReusable) {
        super(actionsRestricted);
        this.weaponEffects = weaponEffects;
        this.isReusable = isReusable;
    }

    public List<String> getWeaponEffects() {
        return weaponEffects;
    }

    public boolean isReusable() {
        return isReusable;
    }

}
