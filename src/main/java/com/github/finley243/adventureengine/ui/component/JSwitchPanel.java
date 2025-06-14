package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.NumericMenuField;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;
import com.google.common.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JSwitchPanel extends JPanel {

    private static final String TOP_LEVEL_MENU = "TOPLEVEL";

    private final CardLayout cardLayout;

    private final EventBus eventBus;
    private final Set<String> validPanels;
    private final Deque<String> panelStack;

    public JSwitchPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.validPanels = new HashSet<>();
        this.panelStack = new ArrayDeque<>();
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
    }

    public void clear() {
        removeAll();
        validPanels.clear();
    }

    public void loadMenu(List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, int endTurnIndex) {
        List<MenuChoice> topLevelActions = new ArrayList<>();
        List<MenuCategory> topLevelCategories = new ArrayList<>();
        Map<String, MenuCategory> categoryByID = new HashMap<>();
        Map<String, List<MenuChoice>> actions = new HashMap<>();
        Map<String, List<MenuCategory>> categories = new HashMap<>();
        Map<String, String> parentCategories = new HashMap<>();
        for (MenuChoice choice : menuChoices) {
            if (choice.getParentCategory() == null) {
                topLevelActions.add(choice);
            } else {
                if (!actions.containsKey(choice.getParentCategory())) {
                    actions.put(choice.getParentCategory(), new ArrayList<>());
                }
                actions.get(choice.getParentCategory()).add(choice);
            }
        }
        for (MenuCategory category : menuCategories) {
            if (category.getParentCategory() == null) {
                topLevelCategories.add(category);
            } else {
                parentCategories.put(category.getCategoryID(), category.getParentCategory());
                if (!categories.containsKey(category.getParentCategory())) {
                    categories.put(category.getParentCategory(), new ArrayList<>());
                }
                categories.get(category.getParentCategory()).add(category);
            }
            categoryByID.put(category.getCategoryID(), category);
        }
        JPanel topLevelPanel = new JChoiceMenuPanel(eventBus, this, null, null, topLevelCategories, topLevelActions, endTurnIndex);
        addChoicePanel(topLevelPanel, TOP_LEVEL_MENU);
        Set<String> combinedCategories = new HashSet<>();
        combinedCategories.addAll(actions.keySet());
        combinedCategories.addAll(categories.keySet());
        for (String categoryID : combinedCategories) {
            String parentCategory = parentCategories.getOrDefault(categoryID, TOP_LEVEL_MENU);
            List<MenuChoice> actionData = actions.getOrDefault(categoryID, new ArrayList<>());
            List<MenuCategory> categoryData = categories.getOrDefault(categoryID, new ArrayList<>());
            String categoryTitle = categoryByID.get(categoryID).getName();
            String lastCategoryID = parentCategory;
            while (lastCategoryID != null && !lastCategoryID.equals(TOP_LEVEL_MENU)) {
                String parentName = categoryByID.get(lastCategoryID).getName();
                if (categoryTitle == null) {
                    categoryTitle = parentName;
                } else {
                    categoryTitle = parentName + " / " + categoryTitle;
                }
                lastCategoryID = parentCategories.get(lastCategoryID);
            }
            JPanel categoryPanel;
            if (categoryByID.get(categoryID).getType() == MenuCategory.CategoryType.INVENTORY_TRANSFER) {
                categoryPanel = new JChoiceMenuDetailsDoublePanel(eventBus, this, parentCategory, categoryTitle, categoryData, actionData, endTurnIndex);
            } else if (categoryByID.get(categoryID).showDetails()) {
                categoryPanel = new JChoiceMenuDetailsPanel(eventBus, this, parentCategory, categoryTitle, categoryData, actionData, endTurnIndex);
            } else {
                categoryPanel = new JChoiceMenuPanel(eventBus, this, parentCategory, categoryTitle, categoryData, actionData, endTurnIndex);
            }
            addChoicePanel(categoryPanel, categoryID);
        }
        switchToLastPanel();
        validate();
    }

    public void loadNumericMenu(List<NumericMenuField> numericFields, int points) {
        JNumericMenuPanel numericPanel = new JNumericMenuPanel(eventBus, this, numericFields, points);
        addChoicePanel(numericPanel, TOP_LEVEL_MENU);
        switchToPanel(TOP_LEVEL_MENU);
        validate();
    }

    public void switchToLastPanel() {
        while (!panelStack.isEmpty()) {
            String lastPanel = panelStack.peek();
            if (validPanels.contains(lastPanel)) {
                cardLayout.show(this, lastPanel);
                return;
            }
            panelStack.pop();
        }
        cardLayout.show(this, TOP_LEVEL_MENU);
        panelStack.push(TOP_LEVEL_MENU);
    }

    public void switchToParentPanel() {
        panelStack.pop(); // Current panel
        switchToLastPanel();
    }

    public void switchToPanel(String panelID) {
        if (validPanels.contains(panelID)) {
            cardLayout.show(this, panelID);
            panelStack.push(panelID);
        } else {
            cardLayout.show(this, TOP_LEVEL_MENU);
            panelStack.clear();
            panelStack.push(TOP_LEVEL_MENU);
        }
        requestFocusInWindow();
    }

    private void addChoicePanel(JPanel panel, String panelID) {
        if (validPanels.contains(panelID)) throw new IllegalArgumentException("Panel with ID " + panelID + " already exists");
        add(panel, panelID);
        validPanels.add(panelID);
    }

}
