package com.github.finley243.adventureengine.item.template;

import java.util.Set;

public class MagazineItemComponentTemplate extends ItemComponentTemplate {

    private final Set<ItemTemplate> ammoTypes;
    private final int magazineSize;
    private final int reloadActionPoints;

    public MagazineItemComponentTemplate(boolean actionsRestricted, Set<ItemTemplate> ammoTypes, int magazineSize, int reloadActionPoints) {
        super(actionsRestricted);
        this.ammoTypes = ammoTypes;
        this.magazineSize = magazineSize;
        this.reloadActionPoints = reloadActionPoints;
    }

    public Set<ItemTemplate> getAmmoTypes() {
        return ammoTypes;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public int getReloadActionPoints() {
        return reloadActionPoints;
    }

}
