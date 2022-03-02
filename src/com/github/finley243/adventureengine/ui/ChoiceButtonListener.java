package com.github.finley243.adventureengine.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.MenuSelectEvent;

public class ChoiceButtonListener implements ActionListener {

	private final Game game;
	private final int index;
	
	public ChoiceButtonListener(Game game, int index) {
		this.game = game;
		this.index = index;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		game.eventBus().post(new MenuSelectEvent(index));
		game.threadControl().unpause();
	}

}
