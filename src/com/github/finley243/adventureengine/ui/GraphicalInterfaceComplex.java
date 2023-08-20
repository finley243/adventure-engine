package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.RenderChoiceMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderNumericMenuEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.ui.TextClearEvent;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class GraphicalInterfaceComplex implements UserInterface {

	private final Game game;

	private final JFrame window;
	private final JGameTextPanel textPanel;
	private final JPanel lowerPanel;
	private final JDetailsPanel detailsPanel;
	private final JSwitchPanel switchPanel;

	public GraphicalInterfaceComplex(Game game) {
		this.game = game;

		this.window = new JFrame(game.data().getConfig("gameName"));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setMinimumSize(new Dimension(500, 600));
		window.setPreferredSize(new Dimension(500, 600));
		window.setLayout(new GridLayout(2, 1));

		this.textPanel = new JGameTextPanel();
		//window.add(textPanel, generateConstraints(0, 0, 2, 1, 1, 1));
		window.add(textPanel);

		this.lowerPanel = new JPanel();
		lowerPanel.setLayout(new GridBagLayout());
		window.add(lowerPanel);

		this.detailsPanel = new JDetailsPanel();
		lowerPanel.add(detailsPanel, generateConstraints(1, 0, 1, 1, 0, 0));

		this.switchPanel = new JSwitchPanel(game);
		lowerPanel.add(switchPanel, generateConstraints(0, 0, 1, 1, 1, 1));

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private GridBagConstraints generateConstraints(int x, int y, int sx, int sy, double wx, double wy) {
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

	}

	@Subscribe
	public void onTextClearEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(textPanel::clearText);
	}
	
}
