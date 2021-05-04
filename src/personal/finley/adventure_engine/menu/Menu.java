package personal.finley.adventure_engine.menu;

import java.util.List;

import personal.finley.adventure_engine.action.Action;

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
