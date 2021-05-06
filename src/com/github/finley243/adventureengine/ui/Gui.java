package com.github.finley243.adventureengine.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.finley243.adventureengine.event.DisplayMenuEvent;
import com.github.finley243.adventureengine.event.DisplayTextEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.google.common.eventbus.Subscribe;

public class Gui implements UserInterface {

	private JFrame window;
	private JScrollPane textScroll;
	private JTextArea textPanel;
	private JPanel choicePanel;
	
	public Gui() {
		this.window = new JFrame("AdventureEngine");
		this.textPanel = new JTextArea();
		this.textScroll = new JScrollPane(textPanel);
		this.choicePanel = new JPanel();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(500, 500));
		
		//window.getContentPane().add(textPanel, BorderLayout.CENTER);
		window.getContentPane().add(textScroll, BorderLayout.CENTER);
		textPanel.setEditable(false);
		textPanel.setLineWrap(true);
		textPanel.setWrapStyleWord(true);
		textPanel.setVisible(true);
		
		window.getContentPane().add(choicePanel, BorderLayout.PAGE_END);
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.PAGE_AXIS));
		choicePanel.setVisible(true);
		
		window.pack();
		// Centers window on screen
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	@Override
	@Subscribe
	public void onTextEvent(DisplayTextEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textPanel.append(event.getText() + "\n\n");
				textPanel.repaint();
				window.pack();
			}
		});
	}

	@Override
	@Subscribe
	public void onMenuEvent(DisplayMenuEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				List<String> choices = event.getChoices();
				choicePanel.removeAll();
				for(int i = 0; i < choices.size(); i++) {
					JButton button = new JButton(choices.get(i));
					button.addActionListener(new ChoiceActionListener(i));
					choicePanel.add(button);
				}
				window.pack();
			}
		});
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				choicePanel.removeAll();
			}
		});
	}
	
}
