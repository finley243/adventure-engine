package com.github.finley243.adventureengine.ui.component;

import com.github.finley243.adventureengine.ui.GraphicalInterfaceComplex;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScrollUI extends BasicScrollBarUI {

    private final boolean centered;
    private final int thumbWidth;
    private final int verticalPadding;

    public CustomScrollUI(boolean centered, int thumbWidth, int verticalPadding) {
        this.centered = centered;
        this.thumbWidth = thumbWidth;
        this.verticalPadding = verticalPadding;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new InvisibleButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new InvisibleButton();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D graphics2D = (Graphics2D) g.create();
        graphics2D.setBackground(GraphicalInterfaceComplex.COLOR_BACKGROUND);
        graphics2D.clearRect(0, 0, c.getWidth(), c.getHeight());
        graphics2D.setColor(isThumbRollover() ? GraphicalInterfaceComplex.COLOR_SCROLL_BAR_HOVER : GraphicalInterfaceComplex.COLOR_SCROLL_BAR);
        int height = Math.max(thumbBounds.height, thumbWidth);
        int x;
        if (centered) { // Centered scroll bar
            x = Math.round(((float) (thumbBounds.width - thumbWidth)) / 2) + thumbBounds.x;
        } else { // Right aligned scroll bar
            x = thumbBounds.width - thumbWidth + thumbBounds.x;
        }
        graphics2D.fillRect(x, thumbBounds.y + verticalPadding, thumbWidth, height - (2 * verticalPadding));
        graphics2D.dispose();
    }

    private static class InvisibleButton extends JButton {
        private InvisibleButton() {
            setPreferredSize(new Dimension());
            setOpaque(false);
            setFocusable(false);
            setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
