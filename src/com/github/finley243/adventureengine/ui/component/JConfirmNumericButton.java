package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.NumericMenuInputEvent;
import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class JConfirmNumericButton extends JPanel {

    private static final String CONFIRM_LABEL = "Confirm";

    private final JLabel labelMain;
    private final JLabel labelDetails;

    private final Game game;
    private final Map<String, JNumericSpinner> spinners;
    private final JSwitchPanel switchPanel;

    private boolean mouseHovering;

    public JConfirmNumericButton(String details, Game game, Map<String, JNumericSpinner> spinners, JSwitchPanel switchPanel) {
        this.game = game;
        this.spinners = spinners;
        this.switchPanel = switchPanel;
        setLayout(new BorderLayout());
        this.labelMain = new JLabel(CONFIRM_LABEL);
        add(labelMain, BorderLayout.LINE_START);
        this.labelDetails = new JLabel(details);
        add(labelDetails, BorderLayout.PAGE_END);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BUTTON_BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
        labelMain.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        labelMain.setFont(GraphicalInterfaceComplex.FONT_BUTTON);
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
        Map<String, Integer> changedValues = new HashMap<>();
        for (Map.Entry<String, JNumericSpinner> entry : spinners.entrySet()) {
            changedValues.put(entry.getKey(), entry.getValue().getValue());
        }
        game.eventBus().post(new NumericMenuInputEvent(changedValues));
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
