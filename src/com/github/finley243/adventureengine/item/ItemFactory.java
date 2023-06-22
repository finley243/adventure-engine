package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.item.template.*;

public class ItemFactory {

	public static Item create(Game game, String statsID) {
		return create(game, game.data().getItemTemplate(statsID));
	}
	
	public static Item create(Game game, ItemTemplate stats) {
		String ID = null;
		if (stats.hasState()) {
			ID = stats.generateInstanceID();
		}
		return create(game, stats, ID);
	}

	public static void load(Game game, String statsID, String ID) {
		create(game, game.data().getItemTemplate(statsID), ID);
	}

	private static Item create(Game game, ItemTemplate stats, String ID) {
		Item item = switch (stats) {
			case ConsumableTemplate ignored -> new ItemConsumable(game, ID, stats.getID());
			case WeaponTemplate ignored -> new ItemWeapon(game, ID, stats.getID());
			case EquippableTemplate ignored -> new ItemEquippable(game, ID, stats.getID());
			case ModTemplate ignored -> new ItemMod(game, ID, stats.getID());
			case AmmoTemplate ignored -> new ItemAmmo(game, ID, stats.getID());
			case MiscTemplate ignored -> new ItemMisc(game, ID, stats.getID());
			default -> null;
		};
		if (item != null && stats.hasState()) {
			game.data().addItemState(ID, item);
		}
		return item;
	}
	
}
