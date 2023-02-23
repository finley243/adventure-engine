package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateKey extends ObjectComponentTemplate {

    private final String prompt;
    private final String phrase;

    public ObjectComponentTemplateKey(Game game, String ID, boolean startEnabled, String name, String prompt, String phrase) {
        super(game, ID, startEnabled, name);
        this.prompt = prompt;
        this.phrase = phrase;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getPhrase() {
        return phrase;
    }

}
