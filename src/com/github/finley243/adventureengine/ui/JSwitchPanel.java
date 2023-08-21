package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JSwitchPanel extends JPanel {

    private static final int SCROLL_BAR_WIDTH_REMOVED = 8;
    private static final int SCROLL_INCREMENT = 5;
    private static final String TOP_LEVEL_MENU = "TOPLEVEL";

    private final JPanel innerPanel;
    private final CardLayout cardLayout;

    private JChoiceButton endTurnButton;

    private final Game game;
    private final Set<String> validPanels;
    private String lastPanel;

    public JSwitchPanel(Game game) {
        this.game = game;
        this.validPanels = new HashSet<>();
        this.innerPanel = new JPanel();
        this.cardLayout = new CardLayout();
        innerPanel.setLayout(cardLayout);
        innerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        innerPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        add(innerPanel, BorderLayout.CENTER);
    }

    public void clear() {
        innerPanel.removeAll();
        validPanels.clear();
        if (endTurnButton != null) {
            remove(endTurnButton);
        }
    }

    public void loadMenu(List<MenuChoice> menuChoices, List<MenuCategory> menuCategories, int endTurnIndex) {
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
        addChoicePanel(topLevelPanel, TOP_LEVEL_MENU);
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
        addEndTurnButton(endTurnIndex);
        switchToPanel(lastPanel);
        validate();
    }

    public void switchToPanel(String panelID) {
        if (validPanels.contains(panelID)) {
            cardLayout.show(innerPanel, panelID);
            lastPanel = panelID;
        } else {
            cardLayout.show(innerPanel, TOP_LEVEL_MENU);
            lastPanel = TOP_LEVEL_MENU;
        }
        requestFocusInWindow();
    }

    private void addChoicePanel(JPanel panel, String panelID) {
        if (validPanels.contains(panelID)) throw new IllegalArgumentException("Panel with ID " + panelID + " already exists");
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollUI(false, 4, 1));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(scrollPane.getVerticalScrollBar().getPreferredSize().width - SCROLL_BAR_WIDTH_REMOVED, scrollPane.getVerticalScrollBar().getPreferredSize().height));
        innerPanel.add(scrollPane, panelID);
        validPanels.add(panelID);
    }

    private void addEndTurnButton(int index) {
        JChoiceButton endTurnButton = new JChoiceButton(GraphicalInterfaceComplex.LABEL_END_TURN, -1, null, game, index, this);
        endTurnButton.setEnabled(index > -1);
        //endTurnButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0), endTurnButton.getBorder()));
        add(endTurnButton, BorderLayout.PAGE_END);
        endTurnButton.setVisible(true);
        this.endTurnButton = endTurnButton;
    }

}
