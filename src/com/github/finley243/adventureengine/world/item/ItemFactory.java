package com.github.finley243.adventureengine.world.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.stats.*;

public class ItemFactory {

	public static Item create(Game game, String statsID, Area area) {
		return create(game, game.data().getItem(statsID), area);
	}
	
	public static Item create(Game game, StatsItem stats, Area area) {
		return create(game, stats, true, stats.generateInstanceID(), area);
	}

	public static Item create(Game game, String statsID, String ID, Area area) {
		return create(game, game.data().getItem(statsID), false, ID, area);
	}

	private static Item create(Game game, StatsItem stats, boolean isGenerated, String ID, Area area) {
		Item item = null;
		if(stats instanceof StatsConsumable) {
			item = new ItemConsumable(game, ID, area, isGenerated, (StatsConsumable) stats);
		} else if(stats instanceof StatsApparel) {
			item = new ItemApparel(game, ID, area, isGenerated, (StatsApparel) stats);
		} else if(stats instanceof StatsWeapon) {
			item = new ItemWeapon(game, ID, area, isGenerated, (StatsWeapon) stats);
		} else if(stats instanceof StatsKey) {
			item = new ItemKey(game, ID, area, isGenerated, (StatsKey) stats);
		} else if(stats instanceof StatsJunk) {
			item = new ItemJunk(game, ID, area, isGenerated, (StatsJunk) stats);
		}
		if(item != null && isGenerated) {
			game.data().addObject(ID, item);
		}
		return item;
	}
	
}
