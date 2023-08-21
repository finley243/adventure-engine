package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class JChoiceMenuDetailsPanel extends JPanel {

    private static final int SCROLL_BAR_WIDTH_REMOVED = 8;
    private static final int SCROLL_INCREMENT = 5;

    // buttonPanel contains buttonScrollPane (which contains buttonInnerPanel, which itself contains listPanel) and endTurnButton
    private final JPanel buttonPanel;
    private final JScrollPane buttonScrollPane;
    private final JPanel buttonInnerPanel;
    private final JPanel listPanel;
    private final JChoiceButton endTurnButton;

    private final JDetailsPanel detailsPanel;

    private final Game game;
    private final JSwitchPanel switchPanel;

    public JChoiceMenuDetailsPanel(Game game, JSwitchPanel switchPanel, String parentCategory, List<MenuCategory> categories, List<MenuChoice> actions, int endTurnIndex) {
        this.game = game;
        this.switchPanel = switchPanel;
        setLayout(new GridBagLayout());
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.buttonInnerPanel = new JPanel();
        buttonInnerPanel.setLayout(new BorderLayout());
        buttonInnerPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.buttonScrollPane = getScrollPane(buttonInnerPanel);
        buttonScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        buttonScrollPane.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());
        listPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.endTurnButton = getEndTurnButton(endTurnIndex);
        this.detailsPanel = new JDetailsPanel();
        int layoutIndex = 0;
        if (parentCategory != null) {
            JComponent backButton = getBackButton();
            listPanel.add(backButton, generateConstraintsButtons(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        categories.sort(Comparator.comparing(MenuCategory::getName));
        for (MenuCategory category : categories) {
            JComponent categoryButton = getCategoryButton(category);
            categoryButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    detailsPanel.setContent(category.getName(), category.getDescription());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    detailsPanel.clear();
                }
            });
            listPanel.add(categoryButton, generateConstraintsButtons(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        actions.sort(Comparator.comparing(MenuChoice::getPrompt));
        for (MenuChoice action : actions) {
            JComponent actionButton = getActionButton(action);
            listPanel.add(actionButton, generateConstraintsButtons(0, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        buttonInnerPanel.add(listPanel, BorderLayout.PAGE_START);
        buttonPanel.add(buttonScrollPane, BorderLayout.CENTER);
        buttonPanel.add(endTurnButton, BorderLayout.PAGE_END);
        add(buttonPanel, generateConstraintsPanels(0, 0, 1, 1, 1, 1));
        add(detailsPanel, generateConstraintsPanels(1, 0, 1, 1, 0, 0));
    }

    private JScrollPane getScrollPane(JPanel viewPanel) {
        JScrollPane scrollPane = new JScrollPane(viewPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollUI(false, 4, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(scrollPane.getVerticalScrollBar().getPreferredSize().width - SCROLL_BAR_WIDTH_REMOVED, scrollPane.getVerticalScrollBar().getPreferredSize().height));
        return scrollPane;
    }

    private JChoiceButton getEndTurnButton(int index) {
        JChoiceButton endTurnButton = new JChoiceButton(GraphicalInterfaceComplex.LABEL_END_TURN, -1, null, game, index, switchPanel);
        endTurnButton.setEnabled(index > -1);
        endTurnButton.setVisible(true);
        return endTurnButton;
    }

    private JComponent getActionButton(MenuChoice action) {
        JChoiceButton actionButton = new JChoiceButton(action.getPrompt(), action.getActionPoints(), action.getDisabledReason(), game, action.getIndex(), switchPanel);
        actionButton.setEnabled(action.isEnabled());
        return actionButton;
    }

    private JComponent getBackButton() {
        return new JBackButton(switchPanel);
    }

    private JComponent getCategoryButton(MenuCategory category) {
        return new JCategoryButton(category.getName(), null, switchPanel, category.getCategoryID());
    }

    private GridBagConstraints generateConstraintsButtons(int x, int y, int sx, int sy, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = sx;
        constraints.gridheight = sy;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 2, 0);
        constraints.anchor = GridBagConstraints.NORTH;
        return constraints;
    }

    private GridBagConstraints generateConstraintsPanels(int x, int y, int sx, int sy, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = sx;
        constraints.gridheight = sy;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = GridBagConstraints.BOTH;
        return constraints;
    }

}
