package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;
import com.google.common.eventbus.EventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class JChoiceMenuDetailsDoublePanel extends JPanel {

    private static final int SCROLL_BAR_WIDTH_REMOVED = 8;
    private static final int SCROLL_INCREMENT = 5;

    private final JDetailsPanel detailsPanel;

    private final EventBus eventBus;
    private final JSwitchPanel switchPanel;

    public JChoiceMenuDetailsDoublePanel(EventBus eventBus, JSwitchPanel switchPanel, String parentCategory, String categoryTitle, List<MenuCategory> categories, List<MenuChoice> actions, int endTurnIndex) {
        this.eventBus = eventBus;
        this.switchPanel = switchPanel;
        setLayout(new GridBagLayout());
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);

        JPanel buttonPanelLeft = new JPanel();
        buttonPanelLeft.setLayout(new BorderLayout());
        buttonPanelLeft.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        buttonPanelLeft.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JPanel buttonInnerPanelLeft = new JPanel();
        buttonInnerPanelLeft.setLayout(new BorderLayout());
        buttonInnerPanelLeft.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JScrollPane buttonScrollPaneLeft = getScrollPane(buttonInnerPanelLeft);
        buttonScrollPaneLeft.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        buttonScrollPaneLeft.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JPanel listPanelLeft = new JPanel();
        listPanelLeft.setLayout(new GridBagLayout());
        listPanelLeft.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JChoiceButton endTurnButton = getEndTurnButton(endTurnIndex);
        buttonInnerPanelLeft.add(listPanelLeft, BorderLayout.PAGE_START);
        buttonPanelLeft.add(buttonScrollPaneLeft, BorderLayout.CENTER);
        buttonPanelLeft.add(endTurnButton, BorderLayout.PAGE_END);

        this.detailsPanel = new JDetailsPanel();

        JPanel buttonPanelRight = new JPanel();
        buttonPanelRight.setLayout(new BorderLayout());
        buttonPanelRight.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        buttonPanelRight.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JPanel buttonInnerPanelRight = new JPanel();
        buttonInnerPanelRight.setLayout(new BorderLayout());
        buttonInnerPanelRight.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JScrollPane buttonScrollPaneRight = getScrollPane(buttonInnerPanelRight);
        buttonScrollPaneRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        buttonScrollPaneRight.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        JPanel listPanelRight = new JPanel();
        listPanelRight.setLayout(new GridBagLayout());
        listPanelRight.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        buttonInnerPanelRight.add(listPanelRight, BorderLayout.PAGE_START);
        buttonPanelRight.add(buttonScrollPaneRight, BorderLayout.CENTER);

        int layoutIndexLeft = 0;
        int layoutIndexRight = 0;
        if (parentCategory != null) {
            JComponent backButton = getBackButton(categoryTitle);
            listPanelLeft.add(backButton, generateConstraintsButtons(0, layoutIndexLeft, 1, 1, 1, 0));
            layoutIndexLeft++;
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
                    //detailsPanel.clear();
                }
            });
            if (category.showOnRight()) {
                listPanelRight.add(categoryButton, generateConstraintsButtons(0, layoutIndexRight, 1, 1, 1, 0));
                layoutIndexRight++;
            } else {
                listPanelLeft.add(categoryButton, generateConstraintsButtons(0, layoutIndexLeft, 1, 1, 1, 0));
                layoutIndexLeft++;
            }
        }
        actions.sort(Comparator.comparing(MenuChoice::getPrompt));
        for (MenuChoice action : actions) {
            JComponent actionButton = getActionButton(action);
            if (action.showOnRight()) {
                listPanelRight.add(actionButton, generateConstraintsButtons(0, layoutIndexRight, 1, 1, 1, 0));
                layoutIndexRight++;
            } else {
                listPanelLeft.add(actionButton, generateConstraintsButtons(0, layoutIndexLeft, 1, 1, 1, 0));
                layoutIndexLeft++;
            }
        }

        add(buttonPanelLeft, generateConstraintsPanels(0, 0, 1, 1, 1, 1));
        add(detailsPanel, generateConstraintsPanels(1, 0, 1, 1, 0, 0));
        add(buttonPanelRight, generateConstraintsPanels(2, 0, 1, 1, 1, 1));
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
        JChoiceButton endTurnButton = new JChoiceButton(GraphicalInterfaceComplex.LABEL_END_TURN, -1, null, eventBus, index, switchPanel);
        endTurnButton.setEnabled(index > -1);
        endTurnButton.setVisible(true);
        return endTurnButton;
    }

    private JComponent getActionButton(MenuChoice action) {
        JChoiceButton actionButton = new JChoiceButton(action.getPrompt(), action.getActionPoints(), action.getDisabledReason(), eventBus, action.getIndex(), switchPanel);
        actionButton.setEnabled(action.isEnabled());
        return actionButton;
    }

    private JComponent getBackButton(String categoryTitle) {
        return new JBackButton(switchPanel, categoryTitle);
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
