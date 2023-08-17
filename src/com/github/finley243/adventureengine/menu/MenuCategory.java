package com.github.finley243.adventureengine.menu;

public class MenuCategory {

    private final String categoryID;
    private final String parentCategory;
    private final String name;

    public MenuCategory(String categoryID, String parentCategory, String name) {
        this.categoryID = categoryID;
        this.parentCategory = parentCategory;
        this.name = name;
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

}
