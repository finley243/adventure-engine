package com.github.finley243.adventureengine.item.template;

public abstract class ItemComponentTemplate {

    private final boolean actionsRestricted;

    public ItemComponentTemplate(boolean actionsRestricted) {
        this.actionsRestricted = actionsRestricted;
    }

    public boolean actionsRestricted() {
        return actionsRestricted;
    }

}
