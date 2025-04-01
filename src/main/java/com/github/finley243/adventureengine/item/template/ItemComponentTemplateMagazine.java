package com.github.finley243.adventureengine.item.template;

import java.util.Set;

public class ItemComponentTemplateMagazine extends ItemComponentTemplate {

    private final Set<String> ammoTypes;
    private final int magazineSize;
    private final int reloadActionPoints;

    public ItemComponentTemplateMagazine(boolean actionsRestricted, Set<String> ammoTypes, int magazineSize, int reloadActionPoints) {
        super(actionsRestricted);
        this.ammoTypes = ammoTypes;
        this.magazineSize = magazineSize;
        this.reloadActionPoints = reloadActionPoints;
    }

    public Set<String> getAmmoTypes() {
        return ammoTypes;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public int getReloadActionPoints() {
        return reloadActionPoints;
    }

}
