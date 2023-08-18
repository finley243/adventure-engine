package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.List;

public class JChoiceMenuPanel extends JPanel {

    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 12);

    private final Game game;
    private final JSwitchPanel switchPanel;

    public JChoiceMenuPanel(Game game, JSwitchPanel switchPanel, String parentCategory, List<MenuCategory> categories, List<MenuChoice> actions) {
        this.game = game;
        this.switchPanel = switchPanel;
        setLayout(new GridBagLayout());
        int layoutIndex = 0;
        if (parentCategory != null) {
            JButton backButton = getBackButton(parentCategory);
            add(backButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        for (MenuCategory category : categories) {
            JButton categoryButton = getCategoryButton(category);
            add(categoryButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        for (MenuChoice action : actions) {
            JButton actionButton = getActionButton(action);
            add(actionButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
    }

    private JButton getActionButton(MenuChoice action) {
        JButton actionButton = new JChoiceButton(action.getPrompt());
        actionButton.setFont(BUTTON_FONT);
        actionButton.setBackground(Color.white);
        actionButton.setForeground(Color.DARK_GRAY);
        actionButton.setToolTipText(action.getDisabledReason());
        actionButton.setHorizontalAlignment(SwingConstants.LEFT);
        actionButton.addActionListener(new ChoiceButtonListener(game, action.getIndex()));
        actionButton.addActionListener(e -> switchPanel.clear());
        actionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                //((JLabel) detailsPanel.getComponent(0)).setText(action.prompt());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                //((JLabel) detailsPanel.getComponent(0)).setText("");
            }
        });
        actionButton.setEnabled(action.isEnabled());
        return actionButton;
    }

    private JButton getBackButton(String parentCategory) {
        JButton backButton = new JChoiceButton("<- Back");
        backButton.setFont(BUTTON_FONT);
        backButton.setBackground(Color.white);
        backButton.setForeground(Color.DARK_GRAY);
        backButton.setHorizontalAlignment(SwingConstants.RIGHT);
        backButton.addActionListener(e -> SwingUtilities.invokeLater(() -> switchPanel.switchToPanel(parentCategory)));
        return backButton;
    }

    private JButton getCategoryButton(MenuCategory category) {
        JButton categoryButton = new JChoiceButton(category.getName() + " ->");
        categoryButton.setFont(BUTTON_FONT);
        categoryButton.setBackground(Color.white);
        categoryButton.setForeground(Color.DARK_GRAY);
        categoryButton.setHorizontalAlignment(SwingConstants.LEFT);
        categoryButton.addActionListener(e -> switchPanel.switchToPanel(category.getCategoryID()));
        return categoryButton;
    }

    private GridBagConstraints generateConstraints(int x, int y, int sx, int sy, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = sx;
        constraints.gridheight = sy;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        return constraints;
    }

}
