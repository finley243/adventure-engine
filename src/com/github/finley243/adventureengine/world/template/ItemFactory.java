package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItem(statsID));
	}
	
	public static Item create(Game game, StatsItem stats) {
		if(stats instanceof StatsConsumable) {
			return new ItemConsumable(game, (StatsConsumable) stats);
		} else if(stats instanceof StatsApparel) {
			return new ItemApparel(game, (StatsApparel) stats);
		} else if(stats instanceof StatsWeapon) {
			return new ItemWeapon(game, (StatsWeapon) stats);
		} else if(stats instanceof StatsKey) {
			return new ItemKey(game, (StatsKey) stats);
		} else if(stats instanceof StatsJunk) {
			return new ItemJunk(game, (StatsJunk) stats);
		}
		return null;
	}
	
}
