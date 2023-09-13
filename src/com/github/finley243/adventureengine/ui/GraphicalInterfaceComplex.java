package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.github.finley243.adventureengine.ui.component.JGameTextPanel;
import com.github.finley243.adventureengine.ui.component.JSwitchPanel;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;

public class GraphicalInterfaceComplex implements UserInterface {

	public static final Color COLOR_BACKGROUND = new Color(242, 242, 242);
	public static final Color COLOR_BORDER = new Color(20, 20, 20);
	public static final Color COLOR_BUTTON = new Color(242, 242, 242);
	public static final Color COLOR_BUTTON_HOVER = new Color(210, 210, 210);
	public static final Color COLOR_BUTTON_CLICK = new Color(185, 185, 210); //new Color(151, 207, 247);
	public static final Color COLOR_BUTTON_DISABLED = new Color(204, 204, 204);
	public static final Color COLOR_BUTTON_BORDER = new Color(20, 20, 20);
	public static final Color COLOR_TEXT = new Color(20, 20, 20);
	public static final Color COLOR_TEXT_ERROR = new Color(209, 32, 29);
	public static final Color COLOR_SCROLL_BAR = new Color(204, 204, 204);
	public static final Color COLOR_SCROLL_BAR_HOVER = new Color(204, 204, 204);

	public static final Font FONT_TEXT = new Font("Arial", Font.PLAIN, 14);
	public static final Font FONT_BUTTON = new Font("Arial", Font.PLAIN, 12);
	public static final Font FONT_BUTTON_SYMBOL = new Font("Arial", Font.PLAIN, 12);
	public static final Font FONT_BUTTON_DESCRIPTION = new Font("Arial", Font.PLAIN, 12);
	public static final Font FONT_DETAILS_TITLE = new Font("Arial", Font.BOLD, 16);
	public static final Font FONT_DETAILS_DESCRIPTION = new Font("Arial", Font.PLAIN, 12);
	public static final float LINE_SPACING = 0.25f;

	public static final String LABEL_END_TURN = "End Turn";
	public static final String LABEL_BACK = "BACK";
	public static final String LABEL_ACTION_POINTS = " AP";

	private final Game game;

	private final JFrame window;
	private final JGameTextPanel textPanel;
	private final JSwitchPanel switchPanel;

	public GraphicalInterfaceComplex(Game game) {
		this.game = game;

		this.window = new JFrame(game.data().getConfig("gameName"));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMinimumSize(new Dimension(500, 600));
		window.setPreferredSize(new Dimension(500, 600));
		window.setLayout(new GridLayout(2, 1));
		window.setBackground(COLOR_BACKGROUND);

		this.textPanel = new JGameTextPanel();
		window.add(textPanel);

		this.switchPanel = new JSwitchPanel(game);
		window.add(switchPanel);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	@Override
	public void onTextEvent(RenderTextEvent e) {
		SwingUtilities.invokeLater(() -> textPanel.appendLine(e.getText()));
	}

	@Override
	public void onMenuEvent(RenderChoiceMenuEvent event) {
		SwingUtilities.invokeLater(() -> switchPanel.loadMenu(event.getMenuChoices(), event.getMenuCategories(), event.getEndTurnIndex()));
	}

	@Override
	public void onNumericMenuEvent(RenderNumericMenuEvent event) {
		SwingUtilities.invokeLater(() -> switchPanel.loadNumericMenu(event.getNumericFields(), event.getPoints()));
	}

	@Subscribe
	public void onTextClearEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(textPanel::clearText);
	}
	
}
