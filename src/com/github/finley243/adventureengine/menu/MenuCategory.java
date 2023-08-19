package com.github.finley243.adventureengine.menu;

public class MenuCategory {

    public enum CategoryType {
        GENERIC, INVENTORY, INVENTORY_TRANSFER, AREA
    }

    private final CategoryType type;
    private final String categoryID;
    private final String parentCategory;
    private final String name;

    public MenuCategory(CategoryType type, String categoryID, String parentCategory, String name) {
        this.type = type;
        this.categoryID = categoryID;
        this.parentCategory = parentCategory;
        this.name = name;
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

}
