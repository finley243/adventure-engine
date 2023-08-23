package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class JDetailsPanel extends JPanel {

    private final JTextPane title;
    private final JTextPane description;

    public JDetailsPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BORDER), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.title = new JTextPane();
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
        this.description = new JTextPane();
        description.setPreferredSize(new Dimension(170, 200));
        description.setMinimumSize(new Dimension(170, 200));
        description.setEditable(false);
        description.setHighlighter(null);
        description.setFont(GraphicalInterfaceComplex.FONT_DETAILS_DESCRIPTION);
        description.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        add(title, generateConstraintsPanels(0, 0, 1, 1, 0, 0));
        add(description, generateConstraintsPanels(0, 1, 1, 1, 1, 1));
    }

    public void clear() {
        title.setText(null);
        description.setText(null);
    }

    public void setContent(String title, String description) {
        StyledDocument titleDoc = this.title.getStyledDocument();
        try {
            titleDoc.remove(0, titleDoc.getLength());
            titleDoc.insertString(titleDoc.getLength(), title, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        StyledDocument descriptionDoc = this.description.getStyledDocument();
        try {
            descriptionDoc.remove(0, descriptionDoc.getLength());
            descriptionDoc.insertString(descriptionDoc.getLength(), description, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private GridBagConstraints generateConstraintsPanels(int x, int y, int sx, int sy, double wx, double wy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = sx;
        constraints.gridheight = sy;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = GridBagConstraints.BOTH;
        return constraints;
    }

}
