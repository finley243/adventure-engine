package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JNumericSpinner extends JPanel {

    private final JLabel label;
    private final JNumericButtonIncrease buttonIncrease;
    private final JNumericButtonDecrease buttonDecrease;

    private final JNumericMenuPanel numericMenuPanel;
    private final int min;
    private final int max;
    private final int maxTotal;

    private int value;

    public JNumericSpinner(int initial, int min, int max, int maxTotal, JNumericMenuPanel numericMenuPanel) {
        this.min = min;
        this.max = max;
        this.maxTotal = maxTotal;
        this.numericMenuPanel = numericMenuPanel;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);

        this.label = new JLabel();
        label.setFont(GraphicalInterfaceComplex.FONT_LABEL);
        label.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        label.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        setValue(initial);

        this.buttonIncrease = new JNumericButtonIncrease(this);
        this.buttonDecrease = new JNumericButtonDecrease(this);

        add(buttonDecrease, generateConstraints(0, 0, 1, 1, 1, 0));
        add(label, generateConstraints(1, 0, 1, 1, 1, 0));
        add(buttonIncrease, generateConstraints(2, 0, 1, 1, 1, 0));
    }

    public void setValue(int value) {
        this.value = value;
        label.setText(Integer.toString(value));
    }

    public int getValue() {
        return value;
    }

    public boolean canIncrease() {
        int currentTotal = numericMenuPanel.getSpinnerTotal();
        return currentTotal < maxTotal && value < max;
    }

    public boolean canDecrease() {
        return value > min;
    }

    public void onValueChanged() {
        numericMenuPanel.updateButtonStates();
    }

    public void updateButtonStates() {
        buttonIncrease.setEnabled(canIncrease());
        buttonDecrease.setEnabled(canDecrease());
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
        constraints.insets = new Insets(0, 0, 2, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        return constraints;
    }

    private static class JNumericButtonIncrease extends JPanel {

        private final JNumericSpinner spinner;

        private boolean mouseHovering;

        public JNumericButtonIncrease(JNumericSpinner spinner) {
            this.spinner = spinner;
            setLayout(new BorderLayout());
            JLabel labelMain = new JLabel("+");
            labelMain.setHorizontalAlignment(SwingConstants.CENTER);
            labelMain.setVerticalAlignment(SwingConstants.CENTER);
            add(labelMain, BorderLayout.CENTER);

            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BUTTON_BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
            labelMain.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
            labelMain.setFont(GraphicalInterfaceComplex.FONT_BUTTON);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_CLICK);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (isEnabled()) {
                        if (mouseHovering) {
                            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_HOVER);
                            onPressed();
                        } else {
                            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_HOVER);
                        mouseHovering = true;
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
                        mouseHovering = false;
                    }
                }
            });
        }

        private void onPressed() {
            if (spinner.canIncrease()) {
                spinner.setValue(spinner.getValue() + 1);
                spinner.onValueChanged();
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
            } else {
                setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_DISABLED);
            }
        }

    }

    private static class JNumericButtonDecrease extends JPanel {

        private final JNumericSpinner spinner;

        private boolean mouseHovering;

        public JNumericButtonDecrease(JNumericSpinner spinner) {
            this.spinner = spinner;
            setLayout(new BorderLayout());
            JLabel labelMain = new JLabel("-");
            labelMain.setHorizontalAlignment(SwingConstants.CENTER);
            labelMain.setVerticalAlignment(SwingConstants.CENTER);
            add(labelMain, BorderLayout.CENTER);

            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BUTTON_BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
            labelMain.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
            labelMain.setFont(GraphicalInterfaceComplex.FONT_BUTTON);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_CLICK);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (isEnabled()) {
                        if (mouseHovering) {
                            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_HOVER);
                            onPressed();
                        } else {
                            setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_HOVER);
                        mouseHovering = true;
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    if (isEnabled()) {
                        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
                        mouseHovering = false;
                    }
                }
            });
        }

        private void onPressed() {
            if (spinner.canDecrease()) {
                spinner.setValue(spinner.getValue() - 1);
                spinner.onValueChanged();
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
            } else {
                setBackground(GraphicalInterfaceComplex.COLOR_BUTTON_DISABLED);
            }
        }

    }

}
