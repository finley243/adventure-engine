package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JSwitchPanel extends JPanel {

    private static final String TOP_LEVEL_MENU = "TOPLEVEL";

    private final Game game;
    private final CardLayout cardLayout;
    private final Set<String> validPanels;
    private String lastPanel;

    public JSwitchPanel(Game game) {
        this.game = game;
        this.cardLayout = new CardLayout();
        this.validPanels = new HashSet<>();
        setLayout(cardLayout);
    }

    public void clear() {
        removeAll();
        validPanels.clear();
    }

    public void loadMenu(List<MenuChoice> menuChoices, List<MenuCategory> menuCategories) {
        List<MenuChoice> topLevelActions = new ArrayList<>();
        List<MenuCategory> topLevelCategories = new ArrayList<>();
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
        }
        JPanel topLevelPanel = new JChoiceMenuPanel(game, this, null, topLevelCategories, topLevelActions);
        addChoicePanelTopLevel(topLevelPanel);
        Set<String> combinedCategories = new HashSet<>();
        combinedCategories.addAll(actions.keySet());
        combinedCategories.addAll(categories.keySet());
        for (String categoryID : combinedCategories) {
            String parentCategory = parentCategories.getOrDefault(categoryID, TOP_LEVEL_MENU);
            List<MenuChoice> actionData = actions.getOrDefault(categoryID, new ArrayList<>());
            List<MenuCategory> categoryData = categories.getOrDefault(categoryID, new ArrayList<>());
            JPanel categoryPanel = new JChoiceMenuPanel(game, this, parentCategory, categoryData, actionData);
            addChoicePanel(categoryPanel, categoryID);
        }
        switchToPanel(lastPanel);
    }

    public void switchToPanel(String panelID) {
        if (validPanels.contains(panelID)) {
            cardLayout.show(this, panelID);
            lastPanel = panelID;
            requestFocusInWindow();
        } else {
            cardLayout.show(this, TOP_LEVEL_MENU);
            lastPanel = TOP_LEVEL_MENU;
            requestFocusInWindow();
        }
    }

    private void addChoicePanel(JPanel panel, String panelID) {
        if (validPanels.contains(panelID)) throw new IllegalArgumentException("Panel with ID " + panelID + " already exists");
        add(panel, panelID);
        validPanels.add(panelID);
    }

    private void addChoicePanelTopLevel(JPanel panel) {
        if (validPanels.contains(TOP_LEVEL_MENU)) throw new IllegalArgumentException("Top-level panel already exists");
        add(panel, TOP_LEVEL_MENU);
        validPanels.add(TOP_LEVEL_MENU);
    }

}
