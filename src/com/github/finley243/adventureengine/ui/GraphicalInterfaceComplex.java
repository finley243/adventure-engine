package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.menu.MenuCategory;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.*;
import java.util.List;

public class GraphicalInterfaceComplex implements UserInterface {

	private static final String TOP_LEVEL_MENU = "TOPLEVEL";

	private final Game game;

	private final JTextPane textPanel;
	private final JPanel detailsPanel;
	private final JPanel switchPanel;

	private final Set<String> validPanels;
	private String lastPanel;

	public GraphicalInterfaceComplex(Game game) {
		this.game = game;
		this.validPanels = new HashSet<>();
		lastPanel = TOP_LEVEL_MENU;

		JFrame window = new JFrame(game.data().getConfig("gameName"));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setPreferredSize(new Dimension(600, 700));
		window.setLayout(new GridBagLayout());

		this.textPanel = new JTextPane();
		textPanel.setEditable(false);
		window.getContentPane().add(textPanel, generateConstraints(0, 0, 3, 1, 1, 1.5));

		this.detailsPanel = getDetailsPanel();
		window.getContentPane().add(detailsPanel, generateConstraints(1, 1, 1, 1, 1, 1));

		this.switchPanel = new JPanel();
		switchPanel.setLayout(new CardLayout());
		JScrollPane menuScrollPane = new JScrollPane(switchPanel);
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

	private void switchToPanel(String panel) {
		if (validPanels.contains(panel)) {
			((CardLayout) switchPanel.getLayout()).show(switchPanel, panel);
			lastPanel = panel;
			switchPanel.requestFocusInWindow();
		} else {
			((CardLayout) switchPanel.getLayout()).show(switchPanel, TOP_LEVEL_MENU);
			lastPanel = TOP_LEVEL_MENU;
			switchPanel.requestFocusInWindow();
		}
	}

	private JPanel getMenuPanel(String parentCategory, List<CategoryData> categories, List<ActionData> actions) {
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new GridBagLayout());
		int layoutIndex = 0;
		if (parentCategory != null) {
			JButton backButton = getBackButton(parentCategory);
			menuPanel.add(backButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
			layoutIndex++;
		}
		for (CategoryData category : categories) {
			JButton categoryButton = getCategoryButton(category);
			menuPanel.add(categoryButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
			layoutIndex++;
		}
		for (ActionData action : actions) {
			JButton actionButton = getActionButton(action);
			menuPanel.add(actionButton, generateConstraints(0, layoutIndex, 1, 1, 1, 0));
			layoutIndex++;
		}
		return menuPanel;
	}

	private void clearMenu() {
		SwingUtilities.invokeLater(() -> {
			switchPanel.removeAll();
			((JLabel) detailsPanel.getComponent(0)).setText("");
			validPanels.clear();
		});
	}

	private JButton getActionButton(ActionData action) {
		JButton actionButton = new JButton(action.prompt());
		actionButton.addActionListener(new ChoiceButtonListener(game, action.index()));
		actionButton.addActionListener(e -> clearMenu());
		actionButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				//((JLabel) detailsPanel.getComponent(0)).setText(action.prompt());
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				//((JLabel) detailsPanel.getComponent(0)).setText("");
			}
		});
		actionButton.setEnabled(action.enable());
		return actionButton;
	}

	private JButton getBackButton(String parentCategory) {
		JButton backButton = new JButton("<- Back");
		backButton.addActionListener(e -> SwingUtilities.invokeLater(() -> switchToPanel(parentCategory)));
		return backButton;
	}

	private JButton getCategoryButton(CategoryData category) {
		JButton categoryButton = new JButton(category.prompt() + " ->");
		categoryButton.addActionListener(e -> switchToPanel(category.ID()));
		return categoryButton;
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
			List<ActionData> topLevelActions = new ArrayList<>();
			List<CategoryData> topLevelCategories = new ArrayList<>();
			Map<String, List<ActionData>> actions = new HashMap<>();
			Map<String, List<CategoryData>> categories = new HashMap<>();
			Map<String, String> parentCategories = new HashMap<>();
			for (MenuChoice choice : event.getMenuChoices()) {
				if (choice.getParentCategory() == null) {
					topLevelActions.add(new ActionData(choice.getIndex(), choice.getPrompt(), choice.isEnabled()));
				} else {
					if (!actions.containsKey(choice.getParentCategory())) {
						actions.put(choice.getParentCategory(), new ArrayList<>());
					}
					actions.get(choice.getParentCategory()).add(new ActionData(choice.getIndex(), choice.getPrompt(), choice.isEnabled()));
				}
			}
			for (MenuCategory category : event.getMenuCategories()) {
				if (category.getParentCategory() == null) {
					topLevelCategories.add(new CategoryData(category.getCategoryID(), category.getName()));
				} else {
					parentCategories.put(category.getCategoryID(), category.getParentCategory());
					if (!categories.containsKey(category.getParentCategory())) {
						categories.put(category.getParentCategory(), new ArrayList<>());
					}
					categories.get(category.getParentCategory()).add(new CategoryData(category.getCategoryID(), category.getName()));
				}
			}
			JPanel topLevelPanel = getMenuPanel(null, topLevelCategories, topLevelActions);
			switchPanel.add(topLevelPanel, TOP_LEVEL_MENU);
			validPanels.add(TOP_LEVEL_MENU);
			Set<String> combinedCategories = new HashSet<>();
			combinedCategories.addAll(actions.keySet());
			combinedCategories.addAll(categories.keySet());
			for (String categoryID : combinedCategories) {
				String parentCategory = parentCategories.getOrDefault(categoryID, TOP_LEVEL_MENU);
				List<ActionData> actionData = actions.getOrDefault(categoryID, List.of());
				List<CategoryData> categoryData = categories.getOrDefault(categoryID, List.of());
				System.out.println("CATEGORY: " + categoryID + " - PARENT: " + parentCategory + " - ACTIONS: " + actionData.size());
				JPanel categoryPanel = getMenuPanel(parentCategory, categoryData, actionData);
				switchPanel.add(categoryPanel, categoryID);
				validPanels.add(categoryID);
			}
			switchToPanel(lastPanel);
		});
	}

	@Override
	public void onNumericMenuEvent(RenderNumericMenuEvent event) {

	}

	@Subscribe
	public void onTextClearEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(() -> textPanel.setText(null));
	}

	/*@Subscribe
	public void onMenuSelectEvent(ChoiceMenuInputEvent e) {
		SwingUtilities.invokeLater(() -> {
			clearMenu();
			switchPanel.repaint();
		});
	}*/

	/*@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuInputEvent e) {
		SwingUtilities.invokeLater(this::clearMenu);
	}*/

	// TODO - Delete these private records and switch to MenuChoice and MenuCategory classes
	private record ActionData(int index, String prompt, boolean enable) {}

	private record CategoryData(String ID, String prompt) {}
	
}
