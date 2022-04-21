package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.template.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItem(statsID));
	}
	
	public static Item create(Game game, ItemTemplate stats) {
		String ID = null;
		if (stats.hasState()) {
			ID = stats.generateInstanceID();
		}
		return create(game, stats, ID);
	}

	public static void load(Game game, String statsID, String ID) {
		create(game, game.data().getItem(statsID), ID);
	}

	private static Item create(Game game, ItemTemplate stats, String ID) {
		Item item = null;
		if(stats instanceof ConsumableTemplate) {
			item = new ItemConsumable(game, ID, (ConsumableTemplate) stats);
		} else if(stats instanceof ApparelTemplate) {
			item = new ItemApparel(game, ID, (ApparelTemplate) stats);
		} else if(stats instanceof WeaponTemplate) {
			item = new ItemWeapon(game, ID, (WeaponTemplate) stats);
		} else if(stats instanceof MiscTemplate) {
			item = new ItemMisc(game, ID, (MiscTemplate) stats);
		} else if(stats instanceof NoteTemplate) {
			item = new ItemNote(game, ID, (NoteTemplate) stats);
		}
		if(item != null && stats.hasState()) {
			game.data().addItemState(ID, item);
		}
		return item;
	}
	
}
