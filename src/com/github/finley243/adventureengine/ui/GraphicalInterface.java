package com.github.finley243.adventureengine.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.finley243.adventureengine.event.TextClearEvent;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.google.common.eventbus.Subscribe;

public class GraphicalInterface implements UserInterface {

	private JFrame window;
	private JTabbedPane tabPane;
	private JTextArea textPanel;
	private JScrollPane historyScroll;
	private JTextArea historyPanel;
	private JScrollPane choiceScroll;
	private JPanel choicePanel;
	
	public GraphicalInterface() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		this.window = new JFrame("AdventureEngine");
		this.tabPane = new JTabbedPane();
		this.textPanel = new JTextArea();
		this.historyPanel = new JTextArea();
		this.historyScroll = new JScrollPane(historyPanel);
		this.choicePanel = new JPanel();
		this.choiceScroll = new JScrollPane(choicePanel);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setPreferredSize(new Dimension(500, 600));
		
		//window.getContentPane().add(historyScroll, BorderLayout.CENTER);
		tabPane.addTab("Current", textPanel);
		tabPane.addTab("History", historyScroll);
		window.getContentPane().add(tabPane, BorderLayout.CENTER);
		textPanel.setEditable(false);
		textPanel.setLineWrap(true);
		textPanel.setWrapStyleWord(true);
		textPanel.setFont(historyPanel.getFont().deriveFont(14f));
		textPanel.setVisible(true);
		historyPanel.setEditable(false);
		historyPanel.setLineWrap(true);
		historyPanel.setWrapStyleWord(true);
		historyPanel.setFont(historyPanel.getFont().deriveFont(14f));
		historyPanel.setVisible(true);
		
		window.getContentPane().add(choiceScroll, BorderLayout.PAGE_END);
		choiceScroll.setPreferredSize(new Dimension(500, 200));
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.PAGE_AXIS));
		choicePanel.setVisible(true);
		
		window.pack();
		// Centers window on screen
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	private List<String[]> getUniqueMenuStructures(List<String[]> menuStructures) {
		List<String[]> uniqueStructures = new ArrayList<String[]>();
		for(String[] menuStructure : menuStructures) {
			boolean unique = true;
			for(String[] menu : uniqueStructures) {
				if(menuStructure.length == 0 || Arrays.equals(menu, menuStructure)) {
					unique = false;
					break;
				}
			}
			if(unique) {
				uniqueStructures.add(menuStructure);
			}
		}
		return uniqueStructures;
	}
	
	private boolean containsMenuStructure(List<String[]> existingStructures, String[] menuStructure) {
		for(String[] existingStructure : existingStructures) {
			if(Arrays.equals(existingStructure, menuStructure)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Subscribe
	public void onTextEvent(RenderTextEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textPanel.append(event.getText() + "\n");
				textPanel.repaint();
				historyPanel.append(event.getText() + "\n");
				historyPanel.repaint();
				window.repaint();
			}
		});
	}

	@Override
	@Subscribe
	public void onMenuEvent(RenderMenuEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				List<String> choices = event.getChoices();
				choicePanel.removeAll();
				for(int i = 0; i < choices.size(); i++) {
					JButton button = new JButton(choices.get(i));
					button.addActionListener(new ChoiceButtonListener(i));
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
				choicePanel.repaint();
			}
		});
	}
	
	@Subscribe
	public void onEndRoundEvent(TextClearEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textPanel.setText(null);
			}
		});
	}
	
}
