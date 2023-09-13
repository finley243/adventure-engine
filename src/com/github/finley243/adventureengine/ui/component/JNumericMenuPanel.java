package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.menu.NumericMenuField;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JNumericMenuPanel extends JPanel {

    private static final int SCROLL_BAR_WIDTH_REMOVED = 8;
    private static final int SCROLL_INCREMENT = 5;

    // spinnerPanel contains spinnerScrollPane (which contains spinnerInnerPanel, which itself contains listPanel) and confirmButton
    private final JPanel spinnerPanel;
    private final JScrollPane spinnerScrollPane;
    private final JPanel spinnerInnerPanel;
    private final JPanel listPanel;
    private final JConfirmNumericButton confirmButton;

    private final Map<String, JNumericSpinner> spinners;
    private final List<NumericMenuField> numericFields;
    private final int points;
    private final int maxTotal;

    private final Game game;
    private final JSwitchPanel switchPanel;

    public JNumericMenuPanel(Game game, JSwitchPanel switchPanel, List<NumericMenuField> numericFields, int points) {
        this.game = game;
        this.switchPanel = switchPanel;
        this.numericFields = numericFields;
        this.points = points;

        int initialSum = 0;
        for (NumericMenuField numericField : numericFields) {
            initialSum += numericField.getInitial();
        }
        this.maxTotal = points + initialSum;

        setLayout(new GridBagLayout());
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new BorderLayout());
        spinnerPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.spinnerInnerPanel = new JPanel();
        spinnerInnerPanel.setLayout(new BorderLayout());
        spinnerInnerPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.spinnerScrollPane = getScrollPane(spinnerInnerPanel);
        spinnerScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        spinnerScrollPane.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.listPanel = new JPanel();
        listPanel.setLayout(new GridBagLayout());
        listPanel.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.spinners = new HashMap<>();
        int layoutIndex = 0;
        numericFields.sort(Comparator.comparing(NumericMenuField::getName));
        for (NumericMenuField numericField : numericFields) {
            JLabel spinnerLabel = getSpinnerLabel(numericField.getName());
            JNumericSpinner spinner = new JNumericSpinner(numericField.getInitial(), numericField.getMin(), numericField.getMax(), maxTotal, this);
            spinners.put(numericField.getID(), spinner);
            listPanel.add(spinnerLabel, generateConstraintsButtons(0, layoutIndex, 1, 1, 1, 0));
            listPanel.add(spinner, generateConstraintsButtons(1, layoutIndex, 1, 1, 1, 0));
            layoutIndex++;
        }
        this.confirmButton = new JConfirmNumericButton(null, game, spinners, switchPanel);
        spinnerInnerPanel.add(listPanel, BorderLayout.PAGE_START);
        spinnerPanel.add(spinnerScrollPane, BorderLayout.CENTER);
        spinnerPanel.add(confirmButton, BorderLayout.PAGE_END);
        add(spinnerPanel, generateConstraintsPanels(0, 0, 1, 1, 1, 1));
        updateButtonStates();
    }

    public int getSpinnerTotal() {
        int currentTotal = 0;
        for (JNumericSpinner currentSpinner : spinners.values()) {
            currentTotal += currentSpinner.getValue();
        }
        return currentTotal;
    }

    public void updateButtonStates() {
        for (JNumericSpinner spinner : spinners.values()) {
            spinner.updateButtonStates();
        }
        confirmButton.setEnabled(getSpinnerTotal() == maxTotal);
    }

    private JLabel getSpinnerLabel(String name) {
        JLabel spinnerLabel = new JLabel(name);
        spinnerLabel.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        spinnerLabel.setFont(GraphicalInterfaceComplex.FONT_LABEL);
        return spinnerLabel;
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
        constraints.anchor = GridBagConstraints.EAST;
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
