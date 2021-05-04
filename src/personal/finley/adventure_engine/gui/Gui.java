package personal.finley.adventure_engine.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Gui {

	private JFrame window;
	private JTextArea textArea;
	private JPanel choicePanel;
	
	public Gui() {
		this.window = new JFrame("AdventureEngine");
		this.textArea = new JTextArea();
		this.choicePanel = new JPanel();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(600, 600);
		// Centers window on screen
		window.setLocationRelativeTo(null);
		
		textArea.setEditable(false);
		window.getContentPane().add(textArea);
		textArea.setVisible(true);
		
		window.getContentPane().add(choicePanel);
		choicePanel.setVisible(true);
		
		window.setVisible(true);
	}
	
}
