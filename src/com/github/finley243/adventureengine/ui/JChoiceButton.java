package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.ChoiceMenuInputEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JChoiceButton extends JPanel {

    public static final Color BACKGROUND = new Color(242, 242, 242);
    public static final Color BACKGROUND_HOVER = new Color(220, 220, 220);
    public static final Color BACKGROUND_CLICK = new Color(151, 207, 247);
    public static final Color BACKGROUND_DISABLED = new Color(204, 204, 204);
    public static final Color TEXT = new Color(20, 20, 20);
    public static final Color TEXT_ERROR = new Color(209, 32, 29);

    public static final Font FONT_MAIN = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_ACTION_POINTS = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_ERROR = new Font("Arial", Font.PLAIN, 12);

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
        String actionPointText = actionPoints == -1 || actionPoints == 0 ? null : actionPoints + " AP";
        this.labelActionPoints = new JLabel(actionPointText);
        add(labelActionPoints, BorderLayout.LINE_END);
        this.labelDetails = new JLabel(details);
        add(labelDetails, BorderLayout.PAGE_END);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(BACKGROUND);
        labelMain.setForeground(TEXT);
        labelMain.setFont(FONT_MAIN);
        labelActionPoints.setForeground(TEXT);
        labelActionPoints.setFont(FONT_ACTION_POINTS);
        labelDetails.setForeground(TEXT_ERROR);
        labelDetails.setFont(FONT_ERROR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (isEnabled()) {
                    setBackground(BACKGROUND_CLICK);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (isEnabled()) {
                    if (mouseHovering) {
                        setBackground(BACKGROUND_HOVER);
                        onPressed();
                    } else {
                        setBackground(BACKGROUND);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (isEnabled()) {
                    setBackground(BACKGROUND_HOVER);
                    mouseHovering = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (isEnabled()) {
                    setBackground(BACKGROUND);
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
            setBackground(BACKGROUND);
        } else {
            setBackground(BACKGROUND_DISABLED);
        }
    }

}
