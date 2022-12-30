package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.textgen.LangUtils;

public class MenuChoice implements Comparable<MenuChoice> {

    private int index;
    private final String prompt;
    private final boolean enabled;
    // TODO - Find way to store unique IDs alongside display names for category, to allow displaying multiple items with the same name
    private final String[] path;
    private final String[] parserPrompts;

    public MenuChoice(String prompt, boolean enabled, String[] path, String[] parserPrompts) {
        this.prompt = prompt;
        this.enabled = enabled;
        this.path = path;
        this.parserPrompts = parserPrompts;
    }

    public MenuChoice(String prompt, boolean enabled, String[] parserPrompts) {
        this(prompt, enabled, new String[0], parserPrompts);
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
        for(String current : path) {
            fullPrompt.append(LangUtils.titleCase(current)).append(" - ");
        }
        fullPrompt.append(prompt);
        return fullPrompt.toString();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String[] getPath() {
        return path;
    }

    public String[] getParserPrompts() {
        return parserPrompts;
    }

    private String sortingString() {
        StringBuilder builder = new StringBuilder();
        for(String current : path) {
            builder.append(current);
        }
        builder.append(prompt);
        return builder.toString();
    }

    @Override
    public int compareTo(MenuChoice other) {
        return this.sortingString().compareTo(other.sortingString());
    }

}
