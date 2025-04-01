package com.github.finley243.adventureengine.ui;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.*;
import com.github.finley243.adventureengine.menu.NumericMenuField;
import com.google.common.eventbus.Subscribe;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphicalInterfaceNested implements UserInterface {

	private static final String CHOICE_PANEL = "choicePanel";
	private static final String NUMERIC_PANEL = "numericPanel";

	private final Game game;

	private final JFrame window;
	private final CardLayout cardLayout;
	private final JTextPane textPanel;
	private final JTextArea areaPanel;
	private final JTextPane historyPanel;
	private final JPanel menuPanel;
	private final JPanel choicePanel;
	private final JPanel numericPanel;
	
	public GraphicalInterfaceNested(Game game) {
		this.game = game;
		this.window = new JFrame(game.data().getConfig("gameName"));
		this.cardLayout = new CardLayout();
		JTabbedPane tabPane = new JTabbedPane();
		this.textPanel = new JTextPane();
		this.areaPanel = new JTextArea();
		JScrollPane textScroll = new JScrollPane(textPanel);

		this.menuPanel = new JPanel();
		menuPanel.setLayout(cardLayout);

		this.historyPanel = new JTextPane();
		JScrollPane historyScroll = new JScrollPane(historyPanel);
		this.choicePanel = new JPanel();
		menuPanel.add(choicePanel, CHOICE_PANEL);
		this.numericPanel = new JPanel();
		menuPanel.add(numericPanel, NUMERIC_PANEL);
		JScrollPane menuScroll = new JScrollPane(menuPanel);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setPreferredSize(new Dimension(500, 600));
		
		window.getContentPane().add(historyScroll, BorderLayout.CENTER);
		tabPane.addTab("Current", textScroll);
		tabPane.addTab("History", historyScroll);
		window.getContentPane().add(tabPane, BorderLayout.CENTER);
		textPanel.setEditable(false);
		textPanel.setVisible(true);
		historyPanel.setEditable(false);
		historyPanel.setVisible(true);

		areaPanel.setEditable(false);
		areaPanel.setFont(areaPanel.getFont().deriveFont(14f).deriveFont(Font.BOLD));
		window.getContentPane().add(areaPanel, BorderLayout.PAGE_START);

		window.getContentPane().add(menuScroll, BorderLayout.PAGE_END);
		menuScroll.setPreferredSize(new Dimension(500, 250));
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.PAGE_AXIS));
		choicePanel.setVisible(true);
		numericPanel.setLayout(new BoxLayout(numericPanel, BoxLayout.PAGE_AXIS));
		
		window.pack();
		// Centers window on screen
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	@Override
	public void onTextEvent(RenderTextEvent e) {
		SwingUtilities.invokeLater(() -> {
			StyledDocument doc = textPanel.getStyledDocument();
			StyledDocument historyDoc = historyPanel.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, Color.BLACK);
			StyleConstants.setFontSize(attributes, 14);
			try {
				doc.insertString(doc.getLength(), e.getText() + "\n", attributes);
				historyDoc.insertString(historyDoc.getLength(), e.getText() + "\n", attributes);
			} catch (BadLocationException ex) {
				throw new RuntimeException(ex);
			}
			textPanel.repaint();
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
	public void onMenuEvent(RenderChoiceMenuEvent e) {
		/*SwingUtilities.invokeLater(() -> {
			cardLayout.show(menuPanel, CHOICE_PANEL);
			choicePanel.removeAll();
			List<MenuChoice> menuData = e.getMenuChoices();
			Map<String, JPopupMenu> categories = new HashMap<>();
			for (MenuChoice current : menuData) {
				if (current.getPath().length == 0) {
					JButton button = new JButton(current.getPrompt());
					button.addActionListener(new ChoiceButtonListener(game, current.getIndex()));
					button.setEnabled(current.isEnabled());
					choicePanel.add(button);
				} else {
					if (!categories.containsKey(current.getPath()[0])) {
						JButton categoryButton = new JButton(current.getPath()[0]);
						choicePanel.add(categoryButton);
						JPopupMenu menuCategory = new JPopupMenu(current.getPath()[0]);
						categoryButton.addActionListener(eAction -> menuCategory.show(categoryButton, categoryButton.getWidth(), 0));
						categories.put(current.getPath()[0], menuCategory);
					}
					if (current.getPath().length > 1) {
						JMenu parentElement = null;
						for (MenuElement subElement : categories.get(current.getPath()[0]).getSubElements()) {
							if (subElement.getComponent().getName() != null && subElement.getComponent().getName().equalsIgnoreCase(current.getPath()[1])) {
								parentElement = (JMenu) subElement.getComponent();
								break;
							}
						}
						if (parentElement == null) {
							parentElement = new JMenu(current.getPath()[1]);
							parentElement.setName(current.getPath()[1]);
							categories.get(current.getPath()[0]).add(parentElement);
						}
						JMenu lastParent;
						for (int i = 2; i < current.getPath().length; i++) {
							lastParent = parentElement;
							parentElement = null;
							for (Component subComponent : lastParent.getMenuComponents()) {
								if (subComponent instanceof JMenu && subComponent.getName().equalsIgnoreCase(current.getPath()[i])) {
									parentElement = (JMenu) subComponent;
									break;
								}
							}
							if (parentElement == null) {
								parentElement = new JMenu(current.getPath()[i]);
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
		});*/
	}

	@Override
	public void onNumericMenuEvent(RenderNumericMenuEvent event) {
		SwingUtilities.invokeLater(() -> {
			int initialSum = 0;
			for (NumericMenuField numericField : event.getNumericFields()) {
				initialSum += numericField.getInitial();
			}
			final int maxTotal = event.getPoints() + initialSum;
			cardLayout.show(menuPanel, NUMERIC_PANEL);
			Map<String, JSpinner> spinners = new HashMap<>();
			for (NumericMenuField numericField : event.getNumericFields()) {
				JPanel spinnerPanel = new JPanel();
				spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
				SpinnerNumberModel spinnerModel = new SpinnerNumberModel(numericField.getInitial(), numericField.getMin(), numericField.getMax(), 1) {
					@Override
					public Object getNextValue() {
						int currentTotal = 0;
						for (JSpinner currentSpinner : spinners.values()) {
							currentTotal += (int) currentSpinner.getValue();
						}
						if (currentTotal >= maxTotal) {
							return getValue();
						}
						return super.getNextValue();
					}
				};
				JLabel spinnerLabel = new JLabel(numericField.getName());
				JSpinner spinner = new JSpinner(spinnerModel);
				/*spinner.addChangeListener(e -> {
					int currentTotal = 0;
					for (JSpinner currentSpinner : spinners.values()) {
						currentTotal += (int) currentSpinner.getValue();
					}
					if (currentTotal >= maxTotal) {
						for (JSpinner currentSpinner : spinners.values()) {
							currentSpinner.getModel()
						}
					}
				});*/
				spinnerPanel.add(spinnerLabel);
				spinnerPanel.add(spinner);
				numericPanel.add(spinnerPanel);
				spinners.put(numericField.getID(), spinner);
			}
			JButton buttonConfirm = new JButton("Confirm");
			buttonConfirm.addActionListener(e -> {
				Map<String, Integer> changedValues = new HashMap<>();
				for (Map.Entry<String, JSpinner> entry : spinners.entrySet()) {
					changedValues.put(entry.getKey(), (Integer) entry.getValue().getValue());
				}
				game.eventBus().post(new NumericMenuInputEvent(changedValues));
            });
			numericPanel.add(buttonConfirm);
		});
	}
	
	@Subscribe
	public void onMenuSelectEvent(ChoiceMenuInputEvent e) {
		SwingUtilities.invokeLater(() -> {
			choicePanel.removeAll();
			choicePanel.repaint();
		});
	}

	@Subscribe
	public void onNumericMenuConfirmEvent(NumericMenuInputEvent e) {
		SwingUtilities.invokeLater(() -> {
			numericPanel.removeAll();
			numericPanel.repaint();
		});
	}
	
	@Subscribe
	public void onEndRoundEvent(TextClearEvent e) {
		SwingUtilities.invokeLater(() -> textPanel.setText(null));
	}
	
}
