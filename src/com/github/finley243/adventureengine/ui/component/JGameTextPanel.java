package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class JGameTextPanel extends JPanel {

    private static final int SCROLL_BAR_WIDTH_REMOVED = 4;
    private static final int SCROLL_BAR_THUMB_WIDTH = 4;
    private static final int SCROLL_BAR_VERTICAL_PADDING = 4;
    private static final int SCROLL_INCREMENT = 5;

    private final JScrollPane scrollPane;
    private final JTextPane textPane;

    private boolean hasAddedText;

    public JGameTextPanel() {
        this.hasAddedText = false;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5), BorderFactory.createLineBorder(GraphicalInterfaceComplex.COLOR_BORDER)));
        setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setHighlighter(null);
        textPane.setFocusable(false);
        textPane.setBorder(BorderFactory.createEmptyBorder(7, 10, 5, 10));
        textPane.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        this.scrollPane = new JScrollPane(textPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollUI(true, SCROLL_BAR_THUMB_WIDTH, SCROLL_BAR_VERTICAL_PADDING));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(scrollPane.getVerticalScrollBar().getPreferredSize().width - SCROLL_BAR_WIDTH_REMOVED, scrollPane.getVerticalScrollBar().getPreferredSize().height));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendLine(String text) {
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(attributes, GraphicalInterfaceComplex.COLOR_TEXT);
        StyleConstants.setFontSize(attributes, GraphicalInterfaceComplex.FONT_TEXT.getSize());
        StyleConstants.setFontFamily(attributes, GraphicalInterfaceComplex.FONT_TEXT.getFamily());
        StyleConstants.setSpaceBelow(attributes, 50);
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(set, GraphicalInterfaceComplex.LINE_SPACING);
        doc.setParagraphAttributes(0, doc.getLength(), set, false);
        try {
            doc.insertString(doc.getLength(), (hasAddedText ? "\n" : "") + text, attributes);
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
        repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
        hasAddedText = true;
    }

    public void clearText() {
        textPane.setText(null);
        hasAddedText = false;
    }

}
