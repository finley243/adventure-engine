package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JChoiceButton extends JPanel {

    private final JLabel labelMain;
    private final JLabel labelActionPoints;
    private final JLabel labelDetails;

    private final Game game;
    private final int choiceIndex;
    private final JSwitchPanel switchPanel;

    private boolean mouseHovering;

    public JChoiceButton(String label, int actionPoints, String details, Game game, int choiceIndex, JSwitchPanel switchPanel) {
        this.game = game;
        this.choiceIndex = choiceIndex;
        this.switchPanel = switchPanel;
        setLayout(new BorderLayout());
        this.labelMain = new JLabel(label);
        add(labelMain, BorderLayout.LINE_START);
        String actionPointText = actionPoints == -1 || actionPoints == 0 ? null : actionPoints + GraphicalInterfaceComplex.LABEL_ACTION_POINTS;
        this.labelActionPoints = new JLabel(actionPointText);
        add(labelActionPoints, BorderLayout.LINE_END);
        this.labelDetails = new JLabel(details);
        add(labelDetails, BorderLayout.PAGE_END);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BUTTON_BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
        labelMain.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        labelMain.setFont(GraphicalInterfaceComplex.FONT_BUTTON);
        labelActionPoints.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        labelActionPoints.setFont(GraphicalInterfaceComplex.FONT_BUTTON_SYMBOL);
        labelDetails.setForeground(GraphicalInterfaceComplex.COLOR_TEXT_ERROR);
        labelDetails.setFont(GraphicalInterfaceComplex.FONT_BUTTON_DESCRIPTION);
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
        switchPanel.clear();
        game.eventBus().post(new ChoiceMenuInputEvent(choiceIndex));
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
