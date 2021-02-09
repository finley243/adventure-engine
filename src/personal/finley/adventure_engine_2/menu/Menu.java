package personal.finley.adventure_engine_2.menu;

import java.util.List;

import personal.finley.adventure_engine_2.action.IAction;

public class Menu {
	
	public static IAction buildMenu(List<IAction> actions) {
		for(int i = 0; i < actions.size(); i++) {
			System.out.println((i+1) + ") " + actions.get(i).getChoiceName());
		}
		
		int inputInt = UserInput.intInRange(1, actions.size());
		System.out.println();
		//actions.get(inputInt - 1).choose(Data.getPlayer());
		return actions.get(inputInt - 1);
	}
	
}
