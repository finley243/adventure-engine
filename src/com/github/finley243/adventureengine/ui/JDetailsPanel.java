package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import java.awt.*;

public class JDetailsPanel extends JPanel {

    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_DESCRIPTION = new Font("Arial", Font.PLAIN, 12);

    private final JLabel title;
    private final JTextArea description;

    public JDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setHorizontalTextPosition(SwingConstants.LEFT);
        title.setFont(FONT_TITLE);
        this.description = new JTextArea();
        description.setPreferredSize(new Dimension(200, 200));
        description.setEditable(false);
        description.setLineWrap(true);
        description.setFont(FONT_DESCRIPTION);
        add(title);
        add(description);
        setContent("Title", "Something here.\nAnother thing here.\n\nA third detail.");
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
