package com.github.finley243.adventureengine.menu;

public class MenuCategory {

    public enum CategoryType {
        GENERIC, INVENTORY, INVENTORY_TRANSFER, AREA
    }

    private final CategoryType type;
    private final String categoryID;
    private final String parentCategory;
    private final boolean showDetails;
    private final boolean showOnRight;
    private final String name;
    private final String description;

    public MenuCategory(CategoryType type, String categoryID, String parentCategory, boolean showDetails, boolean showOnRight, String name, String description) {
        this.type = type;
        this.categoryID = categoryID;
        this.parentCategory = parentCategory;
        this.showDetails = showDetails;
        this.showOnRight = showOnRight;
        this.name = name;
        this.description = description;
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

    public boolean showDetails() {
        return showDetails;
    }

    public boolean showOnRight() {
        return showOnRight;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
