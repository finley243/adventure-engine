package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItemTemplate(statsID));
	}
	
	public static Item create(Game game, ItemTemplate stats) {
		String ID = stats.generateInstanceID();
		return create(game, stats, ID);
	}

	public static void load(Game game, String statsID, String ID) {
		create(game, game.data().getItemTemplate(statsID), ID);
	}

	private static Item create(Game game, ItemTemplate stats, String ID) {
		if (stats == null) return null;
		Item item = new Item(game, ID, stats.getID());
		game.data().addItemInstance(ID, item);
		item.onInit();
		return item;
	}
	
}
