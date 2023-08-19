package com.github.finley243.adventureengine.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class JGameTextPanel extends JTextPane {

    public JGameTextPanel() {
        setEditable(false);
    }

    public void appendLine(String text) {
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(attributes, Color.BLACK);
        StyleConstants.setFontSize(attributes, 14);
        try {
            doc.insertString(doc.getLength(), text + "\n", attributes);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        repaint();
    }

    public void clearText() {
        setText(null);
    }

}
