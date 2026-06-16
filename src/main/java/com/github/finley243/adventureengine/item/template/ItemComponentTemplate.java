package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.gamedata.Registry;

public abstract class ItemComponentTemplate {

    private final boolean actionsRestricted;

    public ItemComponentTemplate(boolean actionsRestricted) {
        this.actionsRestricted = actionsRestricted;
    }

    public boolean actionsRestricted() {
        return actionsRestricted;
    }

    public void resolveReferences(Registry<ItemTemplate> itemTemplateRegistry) {}

}
