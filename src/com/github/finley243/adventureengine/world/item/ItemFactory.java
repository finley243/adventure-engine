package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.stats.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItem(statsID));
	}
	
	public static Item create(Game game, StatsItem stats) {
		return create(game, stats, true, stats.generateInstanceID());
	}

	public static Item create(Game game, String statsID, String ID) {
		return create(game, game.data().getItem(statsID), false, ID);
	}

	private static Item create(Game game, StatsItem stats, boolean isGenerated, String ID) {
		Item item = null;
		if(stats instanceof StatsConsumable) {
			item = new ItemConsumable(game, ID, isGenerated, (StatsConsumable) stats);
		} else if(stats instanceof StatsApparel) {
			item = new ItemApparel(game, ID, isGenerated, (StatsApparel) stats);
		} else if(stats instanceof StatsWeapon) {
			item = new ItemWeapon(game, ID, isGenerated, (StatsWeapon) stats);
		} else if(stats instanceof StatsKey) {
			item = new ItemKey(game, ID, isGenerated, (StatsKey) stats);
		} else if(stats instanceof StatsJunk) {
			item = new ItemJunk(game, ID, isGenerated, (StatsJunk) stats);
		}
		if(item != null) {
			game.data().addObject(ID, item);
		}
		return item;
	}
	
}
