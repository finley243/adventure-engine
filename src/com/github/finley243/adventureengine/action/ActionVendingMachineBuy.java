package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ActionVendingMachineBuy extends Action {

	private final ObjectVendingMachine vendingMachine;
	private final String itemID;
	
	public ActionVendingMachineBuy(ObjectVendingMachine vendingMachine, String itemID) {
		this.vendingMachine = vendingMachine;
		this.itemID = itemID;
	}
	
	@Override
	public void choose(Actor subject) {
		Item item = ItemFactory.create(itemID);
		subject.adjustMoney(-item.getPrice());
		subject.inventory().addItem(item);
		Context context = new Context(subject, false, item, true, vendingMachine, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("buyFrom"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && subject.getMoney() >= Data.getItem(itemID).getPrice();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Buy " + Data.getItem(itemID).getName(), canChoose(subject), new String[]{vendingMachine.getName()});
	}

}
