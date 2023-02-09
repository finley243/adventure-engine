package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

import java.util.List;

public class ObjectComponentTemplateItemUse extends ObjectComponentTemplate {

    private final String prompt;
    private final List<ItemUseData> itemUseData;
    // TODO - Add possibility to require only one item from the list, rather than all items (separate actions for the player to choose from?)
    private final String phrase;

    public ObjectComponentTemplateItemUse(Game game, String ID, boolean startEnabled, String name, String prompt, List<ItemUseData> itemUseData, String phrase) {
        super(game, ID, startEnabled, name);
        this.prompt = prompt;
        this.itemUseData = itemUseData;
        this.phrase = phrase;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<ItemUseData> getItemUseData() {
        return itemUseData;
    }

    public String getPhrase() {
        return phrase;
    }

    public static class ItemUseData {
        public final String itemID;
        public final int count;
        public final boolean isConsumed;

        public ItemUseData(String itemID, int count, boolean isConsumed) {
            this.itemID = itemID;
            this.count = count;
            this.isConsumed = isConsumed;
        }
    }

}
