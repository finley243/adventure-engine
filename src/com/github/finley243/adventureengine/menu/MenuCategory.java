package com.github.finley243.adventureengine.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuCategory {

    public enum CategoryType {
        GENERIC, INVENTORY, INVENTORY_TRANSFER, AREA
    }

    private final CategoryType type;
    private final String categoryID;
    private final String parentCategory;
    private final String name;

    private final List<MenuChoice> choices;
    private final List<MenuCategory> subCategories;

    public MenuCategory(CategoryType type, String categoryID, String parentCategory, String name) {
        this.type = type;
        this.categoryID = categoryID;
        this.parentCategory = parentCategory;
        this.name = name;
        this.choices = new ArrayList<>();
        this.subCategories = new ArrayList<>();
    }

    public CategoryType getType() {
        return type;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public String getName() {
        return name;
    }

    public void addChoice(MenuChoice choice) {
        choices.add(choice);
    }

    public void addSubCategory(MenuCategory category) {
        subCategories.add(category);
    }

    public List<MenuChoice> getChoices() {
        return choices;
    }

    public List<MenuCategory> getSubCategories() {
        return subCategories;
    }

}
