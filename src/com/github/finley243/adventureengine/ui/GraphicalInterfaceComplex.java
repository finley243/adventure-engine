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
	private final JTextPane textPanel;
	private final JPanel detailsPanel;
	private final JSwitchPanel switchPanel;

	public GraphicalInterfaceComplex(Game game) {
		this.game = game;

		this.window = new JFrame(game.data().getConfig("gameName"));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(600, 700));
		window.setLayout(new GridBagLayout());

		this.textPanel = new JTextPane();
		textPanel.setEditable(false);
		window.getContentPane().add(textPanel, generateConstraints(0, 0, 3, 1, 1, 1.5));

		this.detailsPanel = getDetailsPanel();
		window.getContentPane().add(detailsPanel, generateConstraints(1, 1, 1, 1, 1, 1));

		this.switchPanel = new JSwitchPanel(game);
		JScrollPane menuScrollPane = new JScrollPane(switchPanel);
		menuScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		window.getContentPane().add(menuScrollPane, generateConstraints(0, 1, 1, 1, 1, 1));

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private JPanel getDetailsPanel() {
		JPanel detailsPanel = new JPanel();
		JLabel detailsLabel = new JLabel();
		detailsLabel.setPreferredSize(new Dimension(100, 30));
		detailsLabel.setFont(detailsLabel.getFont().deriveFont(Font.BOLD, 16));
		detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		detailsPanel.add(detailsLabel);
		return detailsPanel;
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
		SwingUtilities.invokeLater(() -> {
			StyledDocument doc = textPanel.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, Color.BLACK);
			StyleConstants.setFontSize(attributes, 14);
			try {
				doc.insertString(doc.getLength(), e.getText() + "\n", attributes);
			} catch (BadLocationException ex) {
				throw new RuntimeException(ex);
			}
			textPanel.repaint();
		});
	}

	@Override
	public void onMenuEvent(RenderChoiceMenuEvent event) {
		SwingUtilities.invokeLater(() -> {
			switchPanel.loadMenu(event.getMenuChoices(), event.getMenuCategories());
			window.pack();
		});
	}

	@Override
	public void onNumericMenuEvent(RenderNumericMenuEvent event) {

	}

	@Subscribe
	public void onTextClearEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(() -> textPanel.setText(null));
	}
	
}
