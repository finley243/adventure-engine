package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.load.GameDataException;

import java.util.HashSet;
import java.util.Set;

public class MagazineItemComponentTemplate extends ItemComponentTemplate {

    private Set<String> ammoTypeIDs;
    private Set<ItemTemplate> ammoTypes;
    private final int magazineSize;
    private final int reloadActionPoints;

    public MagazineItemComponentTemplate(boolean actionsRestricted, Set<String> ammoTypeIDs, int magazineSize, int reloadActionPoints) {
        super(actionsRestricted);
        this.ammoTypeIDs = ammoTypeIDs;
        this.magazineSize = magazineSize;
        this.reloadActionPoints = reloadActionPoints;
    }

    @Override
    public void resolveReferences(Registry<ItemTemplate> itemTemplateRegistry) {
        if (this.ammoTypes != null) throw new IllegalStateException("MagazineItemComponentTemplate ammo types have already been resolved");
        Set<ItemTemplate> ammoTypes = new HashSet<>();
        for (String ammoTypeID : this.ammoTypeIDs) {
            ItemTemplate ammoTypeTemplate = itemTemplateRegistry.getFromID(ammoTypeID);
            if (ammoTypeTemplate == null) throw new GameDataException("MagazineItemComponentTemplate has invalid ammo type: " + ammoTypeID);
            ammoTypes.add(ammoTypeTemplate);
        }
        this.ammoTypes = ammoTypes;
        this.ammoTypeIDs = null;
    }

    public Set<ItemTemplate> getAmmoTypes() {
        if (this.ammoTypes == null) throw new IllegalStateException("MagazineItemComponentTemplate ammo types have not been resolved");
        return ammoTypes;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public int getReloadActionPoints() {
        return reloadActionPoints;
    }

}
