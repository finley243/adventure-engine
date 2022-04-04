package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.template.*;

public class ItemFactory {

	public static Item create(Game game, String statsID, Area area) {
		return create(game, game.data().getItem(statsID), area);
	}
	
	public static Item create(Game game, ItemTemplate stats, Area area) {
		return create(game, stats, true, stats.generateInstanceID(), area);
	}

	public static Item create(Game game, String statsID, String ID, Area area) {
		return create(game, game.data().getItem(statsID), false, ID, area);
	}

	private static Item create(Game game, ItemTemplate stats, boolean isGenerated, String ID, Area area) {
		Item item = null;
		if(stats instanceof ConsumableTemplate) {
			item = new ItemConsumable(game, ID, area, isGenerated, (ConsumableTemplate) stats);
		} else if(stats instanceof ApparelTemplate) {
			item = new ItemApparel(game, ID, area, isGenerated, (ApparelTemplate) stats);
		} else if(stats instanceof WeaponTemplate) {
			item = new ItemWeapon(game, ID, area, isGenerated, (WeaponTemplate) stats);
		} else if(stats instanceof MiscTemplate) {
			item = new ItemMisc(game, ID, area, isGenerated, (MiscTemplate) stats);
		} else if(stats instanceof NoteTemplate) {
			item = new ItemNote(game, ID, area, isGenerated, (NoteTemplate) stats);
		}
		if(item != null && isGenerated) {
			game.data().addObject(ID, item);
		}
		return item;
	}
	
}
