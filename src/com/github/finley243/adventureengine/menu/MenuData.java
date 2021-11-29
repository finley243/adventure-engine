package com.github.finley243.adventureengine.menu;

public class MenuData implements Comparable<MenuData> {

    private int index;
    private final String prompt;
    private final String fullPrompt;
    private final boolean enabled;
    private final String[] category;

    public MenuData(String prompt, String fullPrompt, boolean enabled, String[] category) {
        this.prompt = prompt;
        this.fullPrompt = fullPrompt;
        this.enabled = enabled;
        this.category = category;
    }

    public MenuData(String prompt, String fullPrompt, boolean enabled) {
        this(prompt, fullPrompt, enabled, new String[0]);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getFullPrompt() {
        return fullPrompt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String[] getCategory() {
        return category;
    }

    @Override
    public int compareTo(MenuData other) {
        return Integer.compare(this.getIndex(), other.getIndex());
    }

}
