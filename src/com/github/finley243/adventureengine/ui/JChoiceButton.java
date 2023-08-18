package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JChoiceButton extends JButton {

    public static final Color BACKGROUND = new Color(242, 242, 242);
    public static final Color BACKGROUND_HOVER = new Color(232, 232, 232);
    public static final Color BACKGROUND_CLICK = new Color(98, 144, 195);
    public static final Color BACKGROUND_DISABLED = new Color(204, 204, 204);
    public static final Color TEXT = new Color(20, 20, 20);
    public static final Color TEXT_ERROR = new Color(255, 49, 46);

    public static final Font FONT_MAIN = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_ACTION_POINTS = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_ERROR = new Font("Arial", Font.PLAIN, 12);

    private final JLabel labelMain;
    private final JLabel labelActionPoints;
    private final JLabel labelDetails;

    public JChoiceButton(String label, int actionPoints, String details) {
        //super(s);
        setLayout(new BorderLayout());
        this.labelMain = new JLabel(label);
        add(labelMain, BorderLayout.LINE_START);
        this.labelActionPoints = new JLabel(actionPoints == -1 ? null : Integer.toString(actionPoints));
        add(labelActionPoints, BorderLayout.LINE_END);
        this.labelDetails = new JLabel(details);
        add(labelDetails, BorderLayout.PAGE_END);

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(BACKGROUND);
        labelMain.setForeground(TEXT);
        labelActionPoints.setForeground(TEXT);
        labelDetails.setForeground(TEXT_ERROR);
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
                    setBackground(BACKGROUND);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                if (isEnabled()) {
                    setBackground(BACKGROUND_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                if (isEnabled()) {
                    setBackground(BACKGROUND);
                }
            }
        });
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

    /*@Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paint(g2);
    }*/

}
