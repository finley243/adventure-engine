package com.github.finley243.adventureengine.menu;

import java.util.ArrayList;
import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.DisplayMenuEvent;

public class Menu {
	
	/*
	public static Action buildMenu(List<Action> actions) {
		for(int i = 0; i < actions.size(); i++) {
			System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
		}
		
		int inputInt = InputUtils.intInRange(1, actions.size());
		System.out.println();
		//actions.get(inputInt - 1).choose(Data.getPlayer());
		return actions.get(inputInt - 1);
	}
	*/
	
	public static Action buildMenu(List<Action> actions) {
		List<String> choiceStrings = new ArrayList<String>();
		for(int i = 0; i < actions.size(); i++) {
			//System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
			choiceStrings.add(actions.get(i).getChoiceName());
		}
		Game.EVENT_BUS.post(new DisplayMenuEvent(choiceStrings));
		
		int inputInt = InputUtils.intInRange(1, actions.size());
		System.out.println();
		//actions.get(inputInt - 1).choose(Data.getPlayer());
		return actions.get(inputInt - 1);
	}
	
}
