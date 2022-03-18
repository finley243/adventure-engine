package com.github.finley243.adventureengine.world.template;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.world.item.*;

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
		if(stats instanceof StatsConsumable) {
			return new ItemConsumable(game, ID, isGenerated, (StatsConsumable) stats);
		} else if(stats instanceof StatsApparel) {
			return new ItemApparel(game, ID, isGenerated, (StatsApparel) stats);
		} else if(stats instanceof StatsWeapon) {
			return new ItemWeapon(game, ID, isGenerated, (StatsWeapon) stats);
		} else if(stats instanceof StatsKey) {
			return new ItemKey(game, ID, isGenerated, (StatsKey) stats);
		} else if(stats instanceof StatsJunk) {
			return new ItemJunk(game, ID, isGenerated, (StatsJunk) stats);
		}
		return null;
	}
	
}
