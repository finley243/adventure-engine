package com.github.finley243.adventureengine.menu;

import java.util.List;

public class MenuCategory {

    private final String label;
    private final List<MenuChoice> choices;
    private final List<MenuCategory> subCategories;

    public MenuCategory(String label, List<MenuChoice> choices, List<MenuCategory> subCategories) {
        this.label = label;
        this.choices = choices;
        this.subCategories = subCategories;
    }

    public String getLabel() {
        return label;
    }

    public List<MenuChoice> getChoices() {
        return choices;
    }

    public List<MenuCategory> getSubCategories() {
        return subCategories;
    }

}
