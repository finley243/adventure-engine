package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.menu.data.*;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalInterfaceNested implements UserInterface {
	
	private final JFrame window;
	private final JTextArea textPanel;
	private final JTextArea areaPanel;
	private final JTextArea historyPanel;
	private final JPanel choicePanel;
	
	public GraphicalInterfaceNested() {
		this.window = new JFrame(Data.getConfig("gameName"));
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
	@Subscribe
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
	@Subscribe
	public void onMenuEvent(RenderMenuEvent e) {
		SwingUtilities.invokeLater(() -> {
			choicePanel.removeAll();
			List<MenuDataEquipped> equipped = new ArrayList<>();
			List<MenuDataUsing> using = new ArrayList<>();
			List<MenuDataWorldActor> worldActor = new ArrayList<>();
			List<MenuDataWorldObject> worldObject = new ArrayList<>();
			List<MenuDataInventory> inventory = new ArrayList<>();
			List<MenuDataMove> move = new ArrayList<>();
			List<MenuDataGlobal> global = new ArrayList<>();
			for(MenuData current : e.getMenuData()) {
				if(current instanceof MenuDataEquipped) {
					equipped.add((MenuDataEquipped) current);
				} else if(current instanceof MenuDataUsing) {
					using.add((MenuDataUsing) current);
				} else if(current instanceof MenuDataWorldActor) {
					worldActor.add((MenuDataWorldActor) current);
				} else if(current instanceof MenuDataWorldObject) {
					worldObject.add((MenuDataWorldObject) current);
				} else if(current instanceof MenuDataInventory) {
					inventory.add((MenuDataInventory) current);
				} else if(current instanceof MenuDataMove) {
					move.add((MenuDataMove) current);
				} else {
					global.add((MenuDataGlobal) current);
				}
			}
			if(!equipped.isEmpty()) {
				JButton buttonEquipped = new JButton(LangUtils.titleCase(equipped.get(0).getItem().getName()));
				choicePanel.add(buttonEquipped);
				JPopupMenu menuEquipped = new JPopupMenu();
				buttonEquipped.addActionListener(eAction -> menuEquipped.show(buttonEquipped, buttonEquipped.getWidth(), 0));
				for(MenuDataEquipped current : equipped) {
					JMenuItem menuItem = new JMenuItem(current.getPrompt());
					menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
					menuEquipped.add(menuItem);
				}
			}
			if(!using.isEmpty()) {
				JButton buttonUsing = new JButton(LangUtils.titleCase(using.get(0).getObject().getName()));
				choicePanel.add(buttonUsing);
				JPopupMenu menuUsing = new JPopupMenu();
				buttonUsing.addActionListener(eAction -> menuUsing.show(buttonUsing, buttonUsing.getWidth(), 0));
				for(MenuDataUsing current : using) {
					JMenuItem menuItem = new JMenuItem(current.getPrompt());
					menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
					menuUsing.add(menuItem);
				}
			}
			if(!worldActor.isEmpty() || !worldObject.isEmpty()) {
				if(!worldActor.isEmpty()) {
					Map<Actor, JPopupMenu> targetActors = new HashMap<>();
					for(MenuDataWorldActor current : worldActor) {
						if(!targetActors.containsKey(current.getActor())) {
							JButton actorButton = new JButton(LangUtils.titleCase(current.getActor().getName()));
							choicePanel.add(actorButton);
							JPopupMenu menuCategory = new JPopupMenu(LangUtils.titleCase(current.getActor().getName()));
							actorButton.addActionListener(eAction -> menuCategory.show(actorButton, actorButton.getWidth(), 0));
							targetActors.put(current.getActor(), menuCategory);
						}
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
						targetActors.get(current.getActor()).add(menuItem);
					}
				}
				if(!worldObject.isEmpty()) {
					Map<WorldObject, JPopupMenu> targetObjects = new HashMap<>();
					for(MenuDataWorldObject current : worldObject) {
						if(!targetObjects.containsKey(current.getObject())) {
							JButton objectButton = new JButton(LangUtils.titleCase(current.getObject().getName()));
							choicePanel.add(objectButton);
							JPopupMenu menuCategory = new JPopupMenu(LangUtils.titleCase(current.getObject().getName()));
							objectButton.addActionListener(eAction -> menuCategory.show(objectButton, objectButton.getWidth(), 0));
							targetObjects.put(current.getObject(), menuCategory);
						}
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
						targetObjects.get(current.getObject()).add(menuItem);
					}
				}
			}
			if(!inventory.isEmpty()) {
				JButton buttonInventory = new JButton("Inventory");
				choicePanel.add(buttonInventory);
				JPopupMenu menuInventory = new JPopupMenu();
				buttonInventory.addActionListener(eAction -> menuInventory.show(buttonInventory, buttonInventory.getWidth(), 0));
				Map<Item, JMenu> targetItems = new HashMap<>();
				for(MenuDataInventory current : inventory) {
					if(!targetItems.containsKey(current.getItem())) {
						JMenu menuCategory = new JMenu(LangUtils.titleCase(current.getItem().getName()));
						targetItems.put(current.getItem(), menuCategory);
					}
					JMenuItem menuItem = new JMenuItem(current.getPrompt());
					menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
					targetItems.get(current.getItem()).add(menuItem);
				}
				for(JMenuItem categoryMenu : targetItems.values()) {
					menuInventory.add(categoryMenu);
				}
			}
			if(!move.isEmpty()) {
				JButton buttonMove = new JButton("Move");
				choicePanel.add(buttonMove);
				JPopupMenu menuMove = new JPopupMenu();
				buttonMove.addActionListener(eAction -> menuMove.show(buttonMove, buttonMove.getWidth(), 0));
				for(MenuDataMove current : move) {
					JMenuItem menuItem = new JMenuItem(LangUtils.titleCase(current.getArea().getName()));
					menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
					menuMove.add(menuItem);
				}
			}
			for(MenuDataGlobal current : global) {
				JButton button = new JButton(current.getPrompt());
				button.addActionListener(new ChoiceButtonListener(current.getIndex()));
				choicePanel.add(button);
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
