package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItem(statsID));
	}
	
	public static Item create(Game game, ItemTemplate stats) {
		return create(game, stats, true, stats.generateInstanceID());
	}

	public static Item create(Game game, String statsID, String ID) {
		return create(game, game.data().getItem(statsID), false, ID);
	}

	private static Item create(Game game, ItemTemplate stats, boolean isGenerated, String ID) {
		Item item = null;
		if(stats instanceof ConsumableTemplate) {
			item = new ItemConsumable(game, ID, isGenerated, (ConsumableTemplate) stats);
		} else if(stats instanceof ApparelTemplate) {
			item = new ItemApparel(game, ID, isGenerated, (ApparelTemplate) stats);
		} else if(stats instanceof WeaponTemplate) {
			item = new ItemWeapon(game, ID, isGenerated, (WeaponTemplate) stats);
		} else if(stats instanceof MiscTemplate) {
			item = new ItemMisc(game, ID, isGenerated, (MiscTemplate) stats);
		} else if(stats instanceof NoteTemplate) {
			item = new ItemNote(game, ID, isGenerated, (NoteTemplate) stats);
		}
		//if(item != null && isGenerated) {
			//game.data().addObject(ID, item);
		//}
		if(item != null && stats.hasState()) {
			game.data().addItemState(ID, item);
		}
		return item;
	}
	
}
