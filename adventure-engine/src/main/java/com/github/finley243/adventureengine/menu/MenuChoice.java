package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.action.Action;

public class MenuChoice implements Comparable<MenuChoice> {

    private int index;
    private final String prompt;
    private final Action.CanChooseResult canChooseData;
    private final int actionPoints;
    private final boolean showOnRight;
    private final String parentCategory;
    private final String parserPrompt;

    public MenuChoice(String prompt, Action.CanChooseResult canChooseData, int actionPoints, boolean showOnRight, String parentCategory, String parserPrompt) {
        this.prompt = prompt;
        this.canChooseData = canChooseData;
        this.actionPoints = actionPoints;
        this.showOnRight = showOnRight;
        this.parentCategory = parentCategory;
        this.parserPrompt = parserPrompt;
    }

    public MenuChoice(String prompt, Action.CanChooseResult canChooseData, int actionPoints, boolean showOnRight, String parserPrompt) {
        this(prompt, canChooseData, actionPoints, showOnRight, null, parserPrompt);
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

    public boolean isEnabled() {
        return canChooseData.canChoose();
    }

    public String getDisabledReason() {
        return canChooseData.reason();
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public boolean showOnRight() {
        return showOnRight;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public String getParserPrompt() {
        return parserPrompt;
    }

    private String sortingString() {
        if (parentCategory == null) {
            return prompt;
        }
        return parentCategory + prompt;
    }

    @Override
    public int compareTo(MenuChoice other) {
        return this.sortingString().compareTo(other.sortingString());
    }

}
