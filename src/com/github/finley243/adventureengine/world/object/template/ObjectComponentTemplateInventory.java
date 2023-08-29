package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.action.ActionCustom;
import com.github.finley243.adventureengine.item.LootTable;

import java.util.List;

public class ObjectComponentTemplateInventory extends ObjectComponentTemplate {

    private final LootTable lootTable;
    private final String takePrompt;
    private final String takePhrase;
    private final String storePrompt;
    private final String storePhrase;
    private final boolean enableTake;
    private final boolean enableStore;
    private final List<ActionCustom.CustomActionHolder> perItemActions;

    public ObjectComponentTemplateInventory(boolean startEnabled, boolean actionsRestricted, LootTable lootTable, String takePrompt, String takePhrase, String storePrompt, String storePhrase, boolean enableTake, boolean enableStore, List<ActionCustom.CustomActionHolder> perItemActions) {
        super(startEnabled, actionsRestricted);
        this.lootTable = lootTable;
        this.takePrompt = takePrompt;
        this.takePhrase = takePhrase;
        this.storePrompt = storePrompt;
        this.storePhrase = storePhrase;
        this.enableTake = enableTake;
        this.enableStore = enableStore;
        this.perItemActions = perItemActions;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public String getTakePrompt() {
        return takePrompt;
    }

    public String getTakePhrase() {
        return takePhrase;
    }

    public String getStorePrompt() {
        return storePrompt;
    }

    public String getStorePhrase() {
        return storePhrase;
    }

    public boolean enableTake() {
        return enableTake;
    }

    public boolean enableStore() {
        return enableStore;
    }

    public List<ActionCustom.CustomActionHolder> getPerItemActions() {
        return perItemActions;
    }

}
