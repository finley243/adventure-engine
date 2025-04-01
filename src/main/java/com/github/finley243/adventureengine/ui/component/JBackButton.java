package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JBackButton extends JPanel {

    private final JLabel labelMain;
    private final JLabel labelArrow;

    private final JSwitchPanel switchPanel;

    private boolean mouseHovering;

    public JBackButton(JSwitchPanel switchPanel, String categoryTitle) {
        this.switchPanel = switchPanel;
        setLayout(new BorderLayout());
        this.labelMain = new JLabel(GraphicalInterfaceComplex.LABEL_BACK);
        add(labelMain, BorderLayout.LINE_END);
        this.labelArrow = new JLabel("< " + categoryTitle);
        add(labelArrow, BorderLayout.LINE_START);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BUTTON_BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(GraphicalInterfaceComplex.COLOR_BUTTON);
        labelMain.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        labelMain.setFont(GraphicalInterfaceComplex.FONT_BUTTON);
        labelArrow.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        labelArrow.setFont(GraphicalInterfaceComplex.FONT_BUTTON_SYMBOL);
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
        switchPanel.switchToParentPanel();
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
