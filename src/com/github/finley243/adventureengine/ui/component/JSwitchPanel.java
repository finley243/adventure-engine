package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.NumericMenuInputEvent;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.menu.NumericMenuField;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JSwitchPanel extends JPanel {

    private static final String TOP_LEVEL_MENU = "TOPLEVEL";

    private final CardLayout cardLayout;

    private final Game game;
    private final Set<String> validPanels;
    private final Deque<String> panelStack;

    public JSwitchPanel(Game game) {
        this.game = game;
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
        JPanel topLevelPanel = new JChoiceMenuPanel(game, this, null, topLevelCategories, topLevelActions, endTurnIndex);
        addChoicePanel(topLevelPanel, TOP_LEVEL_MENU);
        Set<String> combinedCategories = new HashSet<>();
        combinedCategories.addAll(actions.keySet());
        combinedCategories.addAll(categories.keySet());
        for (String categoryID : combinedCategories) {
            String parentCategory = parentCategories.getOrDefault(categoryID, TOP_LEVEL_MENU);
            List<MenuChoice> actionData = actions.getOrDefault(categoryID, new ArrayList<>());
            List<MenuCategory> categoryData = categories.getOrDefault(categoryID, new ArrayList<>());
            JPanel categoryPanel;
            if (categoryByID.get(categoryID).getType() == MenuCategory.CategoryType.INVENTORY_TRANSFER) {
                categoryPanel = new JChoiceMenuDetailsDoublePanel(game, this, parentCategory, categoryData, actionData, endTurnIndex);
            } else if (categoryByID.get(categoryID).showDetails()) {
                categoryPanel = new JChoiceMenuDetailsPanel(game, this, parentCategory, categoryData, actionData, endTurnIndex);
            } else {
                categoryPanel = new JChoiceMenuPanel(game, this, parentCategory, categoryData, actionData, endTurnIndex);
            }
            addChoicePanel(categoryPanel, categoryID);
        }
        switchToLastPanel();
        validate();
    }

    public void loadNumericMenu(List<NumericMenuField> numericFields, int points) {
        JPanel numericPanel = new JPanel();
        int initialSum = 0;
        for (NumericMenuField numericField : numericFields) {
            initialSum += numericField.getInitial();
        }
        final int maxTotal = points + initialSum;
        Map<String, JSpinner> spinners = new HashMap<>();
        for (NumericMenuField numericField : numericFields) {
            JPanel spinnerPanel = new JPanel();
            spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(numericField.getInitial(), numericField.getMin(), numericField.getMax(), 1) {
                @Override
                public Object getNextValue() {
                    int currentTotal = 0;
                    for (JSpinner currentSpinner : spinners.values()) {
                        currentTotal += (int) currentSpinner.getValue();
                    }
                    if (currentTotal >= maxTotal) {
                        return getValue();
                    }
                    return super.getNextValue();
                }
            };
            JLabel spinnerLabel = new JLabel(numericField.getName());
            JSpinner spinner = new JSpinner(spinnerModel);
				/*spinner.addChangeListener(e -> {
					int currentTotal = 0;
					for (JSpinner currentSpinner : spinners.values()) {
						currentTotal += (int) currentSpinner.getValue();
					}
					if (currentTotal >= maxTotal) {
						for (JSpinner currentSpinner : spinners.values()) {
							currentSpinner.getModel()
						}
					}
				});*/
            spinnerPanel.add(spinnerLabel);
            spinnerPanel.add(spinner);
            numericPanel.add(spinnerPanel);
            spinners.put(numericField.getID(), spinner);
        }
        JButton buttonConfirm = new JButton("Confirm");
        buttonConfirm.addActionListener(e -> {
            Map<String, Integer> changedValues = new HashMap<>();
            for (Map.Entry<String, JSpinner> entry : spinners.entrySet()) {
                changedValues.put(entry.getKey(), (Integer) entry.getValue().getValue());
            }
            clear();
            game.eventBus().post(new NumericMenuInputEvent(changedValues));
        });
        numericPanel.add(buttonConfirm);
        addChoicePanel(numericPanel, TOP_LEVEL_MENU);
        switchToPanel(TOP_LEVEL_MENU);
        System.out.println("DONE");
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
