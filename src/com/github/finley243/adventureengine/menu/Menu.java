package com.github.finley243.adventureengine.menu;

import java.util.List;

import com.github.finley243.adventureengine.action.Action;

public class Menu {
	
	public static Action buildMenu(List<Action> actions) {
		for(int i = 0; i < actions.size(); i++) {
			System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
		}
		
		int inputInt = UserInput.intInRange(1, actions.size());
		System.out.println();
		//actions.get(inputInt - 1).choose(Data.getPlayer());
		return actions.get(inputInt - 1);
	}
	
}
