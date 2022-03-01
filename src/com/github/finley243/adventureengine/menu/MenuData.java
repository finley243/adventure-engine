package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.textgen.LangUtils;

public class MenuData implements Comparable<MenuData> {

    private int index;
    private final String prompt;
    private final boolean enabled;
    private final String[] category;

    public MenuData(String prompt, boolean enabled, String[] category) {
        this.prompt = prompt;
        this.enabled = enabled;
        this.category = category;
    }

    public MenuData(String prompt, boolean enabled) {
        this(prompt, enabled, new String[0]);
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
        StringBuilder fullPrompt = new StringBuilder();
        for(String current : category) {
            fullPrompt.append(LangUtils.titleCase(current)).append(" - ");
        }
        fullPrompt.append(prompt);
        return fullPrompt.toString();
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
