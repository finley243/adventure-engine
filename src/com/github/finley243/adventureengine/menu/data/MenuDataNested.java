package com.github.finley243.adventureengine.menu.data;

public class MenuDataNested extends MenuData {

    private final String[] category;

    public MenuDataNested(String prompt, String fullPrompt, boolean enabled, String[] category) {
        super(prompt, fullPrompt, enabled);
        this.category = category;
    }

    public MenuDataNested(String prompt, String fullPrompt, boolean enabled) {
        this(prompt, fullPrompt, enabled, new String[0]);
    }

    public String[] getCategory() {
        return category;
    }

}
