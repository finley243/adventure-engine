package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class JDetailsPanel extends JPanel {

    private final JTextPane title;
    private final JTextPane description;

    public JDetailsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BORDER), BorderFactory.createEmptyBorder(5, 5, 5, 5))));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.title = new JTextPane();
        //title.setPreferredSize(new Dimension(170, 0));
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(GraphicalInterfaceComplex.FONT_DETAILS_TITLE);
        title.setForeground(GraphicalInterfaceComplex.COLOR_TEXT);
        title.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        title.setEditable(false);
        title.setHighlighter(null);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setSpaceBelow(attributes, 0.2f);
        StyleConstants.setSpaceAbove(attributes, 0.2f);
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
        title.setParagraphAttributes(attributes, false);
        //title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.description = new JTextPane();
        description.setPreferredSize(new Dimension(170, 200));
        description.setMinimumSize(new Dimension(170, 200));
        description.setEditable(false);
        description.setHighlighter(null);
        //description.setLineWrap(true);
        //description.setWrapStyleWord(true);
        description.setFont(GraphicalInterfaceComplex.FONT_DETAILS_DESCRIPTION);
        //description.setOpaque(false);
        description.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        add(title);
        add(description);
    }

    public void clear() {
        title.setText(null);
        description.setText(null);
    }

    public void setContent(String title, String description) {
        StyledDocument titleDoc = this.title.getStyledDocument();
        try {
            titleDoc.insertString(titleDoc.getLength(), title, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        StyledDocument descriptionDoc = this.description.getStyledDocument();
        try {
            descriptionDoc.insertString(descriptionDoc.getLength(), description, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

}
