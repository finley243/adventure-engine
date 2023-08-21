package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import java.awt.*;

public class JDetailsPanel extends JPanel {

    public static final Color TEXT = new Color(20, 20, 20);

    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_DESCRIPTION = new Font("Arial", Font.PLAIN, 12);

    private final JLabel title;
    private final JTextArea description;

    public JDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TEXT), BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        this.title = new JLabel();
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.description = new JTextArea();
        description.setPreferredSize(new Dimension(200, 200));
        description.setEditable(false);
        description.setHighlighter(null);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(FONT_DESCRIPTION);
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
