package com.github.finley243.adventureengine.item.template;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class ConsumableItemComponentTemplate extends ItemComponentTemplate {

    private final String consumePrompt;
    private final String consumePhrase;
    private final List<Effect> effects;

    public ConsumableItemComponentTemplate(boolean actionsRestricted, String consumePrompt, String consumePhrase, List<Effect> effects) {
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

    public List<Effect> getEffects() {
        return effects;
    }

}
