package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import java.awt.*;

public class JDetailsPanel extends JPanel {

    private final JLabel title;
    private final JTextArea description;

    public JDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BORDER), BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.title = new JLabel();
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(GraphicalInterfaceComplex.FONT_DETAILS_TITLE);
        title.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.description = new JTextArea();
        description.setPreferredSize(new Dimension(180, 200));
        description.setEditable(false);
        description.setHighlighter(null);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(GraphicalInterfaceComplex.FONT_DETAILS_DESCRIPTION);
        description.setOpaque(false);
        add(title);
        add(description);
        setContent("Name", "Description goes here.\n\nCapable of displaying multiple lines.\n\nThese lines can be as long as you want, as the details panel will automatically wrap words to the next line.");
    }

    public void clear() {
        title.setText(null);
        description.setText(null);
    }

    public void setContent(String title, String description) {
        this.title.setText(title);
        this.description.setText(description);
    }

}
