package com.github.finley243.adventureengine.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.MenuSelectEvent;

public class ChoiceButtonListener implements ActionListener {

	private int index;
	
	public ChoiceButtonListener(int index) {
		this.index = index;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Game.EVENT_BUS.post(new MenuSelectEvent(index));
	}

}
