package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalInterfaceNested implements UserInterface {

	private final Game game;

	private final JFrame window;
	private final JTextArea textPanel;
	private final JTextArea areaPanel;
	private final JTextArea historyPanel;
	private final JPanel choicePanel;
	
	public GraphicalInterfaceNested(Game game) {
		this.game = game;
		this.window = new JFrame(game.data().getConfig("gameName"));
		JTabbedPane tabPane = new JTabbedPane();
		this.textPanel = new JTextArea();
		this.areaPanel = new JTextArea();
		JScrollPane textScroll = new JScrollPane(textPanel);

		this.historyPanel = new JTextArea();
		JScrollPane historyScroll = new JScrollPane(historyPanel);
		this.choicePanel = new JPanel();
		JScrollPane choiceScroll = new JScrollPane(choicePanel);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setPreferredSize(new Dimension(500, 600));
		
		//window.getContentPane().add(historyScroll, BorderLayout.CENTER);
		tabPane.addTab("Current", textScroll);
		tabPane.addTab("History", historyScroll);
		window.getContentPane().add(tabPane, BorderLayout.CENTER);
		textPanel.setEditable(false);
		textPanel.setLineWrap(true);
		textPanel.setWrapStyleWord(true);
		textPanel.setFont(textPanel.getFont().deriveFont(14f));
		textPanel.setVisible(true);
		historyPanel.setEditable(false);
		historyPanel.setLineWrap(true);
		historyPanel.setWrapStyleWord(true);
		historyPanel.setFont(historyPanel.getFont().deriveFont(14f));
		historyPanel.setVisible(true);

		areaPanel.setEditable(false);
		areaPanel.setFont(areaPanel.getFont().deriveFont(14f).deriveFont(Font.BOLD));
		window.getContentPane().add(areaPanel, BorderLayout.PAGE_START);
		
		window.getContentPane().add(choiceScroll, BorderLayout.PAGE_END);
		choiceScroll.setPreferredSize(new Dimension(500, 200));
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.PAGE_AXIS));
		choicePanel.setVisible(true);
		
		window.pack();
		// Centers window on screen
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	@Override
	public void onTextEvent(RenderTextEvent e) {
		SwingUtilities.invokeLater(() -> {
			textPanel.append(e.getText() + "\n");
			textPanel.repaint();
			historyPanel.append(e.getText() + "\n");
			historyPanel.repaint();
			window.repaint();
		});
	}

	@Subscribe
	public void onAreaEvent(RenderAreaEvent e) {
		SwingUtilities.invokeLater(() -> {
			areaPanel.setText(e.getRoom() + " (" + e.getArea() + ")");
			areaPanel.repaint();
			window.repaint();
		});
	}

	@Override
	public void onMenuEvent(RenderMenuEvent e) {
		SwingUtilities.invokeLater(() -> {
			choicePanel.removeAll();
			List<MenuChoice> menuData = e.getMenuChoices();
			Map<String, JPopupMenu> categories = new HashMap<>();
			for(MenuChoice current : menuData) {
				if(current.getPath().length == 0) {
					JButton button = new JButton(current.getPrompt());
					button.addActionListener(new ChoiceButtonListener(game, current.getIndex()));
					button.setEnabled(current.isEnabled());
					choicePanel.add(button);
				} else {
					if (!categories.containsKey(current.getPath()[0])) {
						JButton categoryButton = new JButton(LangUtils.titleCase(current.getPath()[0]));
						choicePanel.add(categoryButton);
						JPopupMenu menuCategory = new JPopupMenu(current.getPath()[0]);
						categoryButton.addActionListener(eAction -> menuCategory.show(categoryButton, categoryButton.getWidth(), 0));
						categories.put(current.getPath()[0], menuCategory);
					}
					if(current.getPath().length > 1) {
						JMenu parentElement = null;
						for (MenuElement subElement : categories.get(current.getPath()[0]).getSubElements()) {
							if (subElement.getComponent().getName() != null && subElement.getComponent().getName().equalsIgnoreCase(current.getPath()[1])) {
								parentElement = (JMenu) subElement.getComponent();
								break;
							}
						}
						if (parentElement == null) {
							parentElement = new JMenu(LangUtils.titleCase(current.getPath()[1]));
							parentElement.setName(current.getPath()[1]);
							categories.get(current.getPath()[0]).add(parentElement);
						}
						JMenu lastParent;
						for(int i = 2; i < current.getPath().length; i++) {
							lastParent = parentElement;
							parentElement = null;
							for (Component subComponent : lastParent.getMenuComponents()) {
								if (subComponent instanceof JMenu && subComponent.getName().equalsIgnoreCase(current.getPath()[i])) {
									parentElement = (JMenu) subComponent;
									break;
								}
							}
							if (parentElement == null) {
								parentElement = new JMenu(LangUtils.titleCase(current.getPath()[i]));
								parentElement.setName(current.getPath()[i]);
								lastParent.add(parentElement);
							}
						}
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(game, current.getIndex()));
						menuItem.setEnabled(current.isEnabled());
						parentElement.add(menuItem);
					} else {
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(game, current.getIndex()));
						menuItem.setEnabled(current.isEnabled());
						categories.get(current.getPath()[0]).add(menuItem);
					}
				}
			}
			window.pack();
		});
	}
	
	@Subscribe
	public void onMenuSelectEvent(MenuSelectEvent e) {
		SwingUtilities.invokeLater(() -> {
			choicePanel.removeAll();
			choicePanel.repaint();
		});
	}
	
	@Subscribe
	public void onEndRoundEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(() -> textPanel.setText(null));
	}
	
}
