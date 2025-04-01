package com.github.finley243.adventureengine.item.template;

import java.util.List;

public class ItemComponentTemplateConsumable extends ItemComponentTemplate {

    private final String consumePrompt;
    private final String consumePhrase;
    private final List<String> effects;

    public ItemComponentTemplateConsumable(boolean actionsRestricted, String consumePrompt, String consumePhrase, List<String> effects) {
        super(actionsRestricted);
        this.consumePrompt = consumePrompt;
        this.consumePhrase = consumePhrase;
        this.effects = effects;
    }

    public String getConsumePrompt() {
        return consumePrompt;
    }

    public String getConsumePhrase() {
        return consumePhrase;
    }

    public List<String> getEffects() {
        return effects;
    }

}
