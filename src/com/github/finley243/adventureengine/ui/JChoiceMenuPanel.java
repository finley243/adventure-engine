package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class JChoiceMenuPanel extends JPanel {

    private final Game game;
    private final JPanel innerPanel;
    private final JSwitchPanel switchPanel;

    public JChoiceMenuPanel(Game game, JSwitchPanel switchPanel, String parentCategory, List<MenuCategory> categories, List<MenuChoice> actions) {
        this.game = game;
        this.switchPanel = switchPanel;
        setLayout(new BorderLayout());
        this.innerPanel = new JPanel();
        innerPanel.setLayout(new GridBagLayout());
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        innerPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        int layoutIndex = 0;
        if (parentCategory != null) {
            JComponent backButton = getBackButton(parentCategory);
            innerPanel.add(backButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        categories.sort(Comparator.comparing(MenuCategory::getName));
        for (MenuCategory category : categories) {
            JComponent categoryButton = getCategoryButton(category);
            innerPanel.add(categoryButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        actions.sort(Comparator.comparing(MenuChoice::getPrompt));
        for (MenuChoice action : actions) {
            JComponent actionButton = getActionButton(action);
            innerPanel.add(actionButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        add(innerPanel, BorderLayout.PAGE_START);
    }

    private JComponent getActionButton(MenuChoice action) {
        JChoiceButton actionButton = new JChoiceButton(action.getPrompt(), action.getActionPoints(), action.getDisabledReason(), game, action.getIndex(), switchPanel);
        actionButton.setEnabled(action.isEnabled());
        return actionButton;
    }

    private JComponent getBackButton(String parentCategory) {
        return new JBackButton(switchPanel, parentCategory);
    }

    private JComponent getCategoryButton(MenuCategory category) {
        return new JCategoryButton(category.getName(), null, switchPanel, category.getCategoryID());
    }

    private GridBagConstraints generateConstraints(int x, int y, int sx, int sy, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = sx;
        constraints.gridheight = sy;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(1, 0, 1, 0);
        constraints.anchor = GridBagConstraints.NORTH;
        return constraints;
    }

}
