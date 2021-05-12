package com.github.finley243.adventureengine.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.MenuSelectEvent;
import com.github.finley243.adventureengine.event.RenderMenuEvent;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.TextClearEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.menu.data.MenuDataGlobal;
import com.github.finley243.adventureengine.menu.data.MenuDataInventory;
import com.github.finley243.adventureengine.menu.data.MenuDataMove;
import com.github.finley243.adventureengine.menu.data.MenuDataUsing;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldActor;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.google.common.eventbus.Subscribe;

public class GraphicalInterfaceNested implements UserInterface {

	private JFrame window;
	private JTabbedPane tabPane;
	private JTextArea textPanel;
	private JScrollPane historyScroll;
	private JTextArea historyPanel;
	private JScrollPane choiceScroll;
	private JPanel choicePanel;
	
	public GraphicalInterfaceNested() {
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
				choicePanel.removeAll();
				//List<String> choices = event.getChoices();
				List<MenuDataEquipped> equipped = new ArrayList<MenuDataEquipped>();
				List<MenuDataUsing> using = new ArrayList<MenuDataUsing>();
				List<MenuDataWorldActor> worldActor = new ArrayList<MenuDataWorldActor>();
				List<MenuDataWorldObject> worldObject = new ArrayList<MenuDataWorldObject>();
				List<MenuDataInventory> inventory = new ArrayList<MenuDataInventory>();
				List<MenuDataMove> move = new ArrayList<MenuDataMove>();
				List<MenuDataGlobal> global = new ArrayList<MenuDataGlobal>();
				for(MenuData current : event.getMenuData()) {
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
					buttonEquipped.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuEquipped.show(buttonEquipped, buttonEquipped.getWidth(), 0);
						}
					});
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
					buttonUsing.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuUsing.show(buttonUsing, buttonUsing.getWidth(), 0);
						}
					});
					for(MenuDataUsing current : using) {
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
						menuUsing.add(menuItem);
					}
				}
				if(!worldActor.isEmpty()) {
					JButton buttonWorldActor = new JButton("Actors");
					choicePanel.add(buttonWorldActor);
					JPopupMenu menuWorldActor = new JPopupMenu();
					buttonWorldActor.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuWorldActor.show(buttonWorldActor, buttonWorldActor.getWidth(), 0);
						}
					});
					Map<Actor, JMenu> targetActors = new HashMap<Actor, JMenu>();
					for(MenuDataWorldActor current : worldActor) {
						if(!targetActors.containsKey(current.getActor())) {
							JMenu menuCategory = new JMenu(LangUtils.titleCase(current.getActor().getName()));
							targetActors.put(current.getActor(), menuCategory);
						}
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
						targetActors.get(current.getActor()).add(menuItem);
					}
					for(JMenuItem categoryMenu : targetActors.values()) {
						menuWorldActor.add(categoryMenu);
					}
				}
				if(!worldObject.isEmpty()) {
					JButton buttonWorldObject = new JButton("Objects");
					choicePanel.add(buttonWorldObject);
					JPopupMenu menuWorldObject = new JPopupMenu();
					buttonWorldObject.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuWorldObject.show(buttonWorldObject, buttonWorldObject.getWidth(), 0);
						}
					});
					Map<WorldObject, JMenu> targetObjects = new HashMap<WorldObject, JMenu>();
					for(MenuDataWorldObject current : worldObject) {
						if(!targetObjects.containsKey(current.getObject())) {
							JMenu menuCategory = new JMenu(LangUtils.titleCase(current.getObject().getName()));
							targetObjects.put(current.getObject(), menuCategory);
						}
						JMenuItem menuItem = new JMenuItem(current.getPrompt());
						menuItem.addActionListener(new ChoiceButtonListener(current.getIndex()));
						targetObjects.get(current.getObject()).add(menuItem);
					}
					for(JMenuItem categoryMenu : targetObjects.values()) {
						menuWorldObject.add(categoryMenu);
					}
				}
				if(!inventory.isEmpty()) {
					JButton buttonInventory = new JButton("Inventory");
					choicePanel.add(buttonInventory);
					JPopupMenu menuInventory = new JPopupMenu();
					buttonInventory.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuInventory.show(buttonInventory, buttonInventory.getWidth(), 0);
						}
					});
					Map<Item, JMenu> targetItems = new HashMap<Item, JMenu>();
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
					buttonMove.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							menuMove.show(buttonMove, buttonMove.getWidth(), 0);
						}
					});
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
