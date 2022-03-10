package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
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
		Item item = ItemFactory.create(subject.game(), itemID);
		subject.adjustMoney(-item.getPrice());
		subject.inventory().addItem(item);
		Context context = new Context(subject, item, vendingMachine);
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("buyFrom"), context, this, subject));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled && subject.getMoney() >= subject.game().data().getItem(itemID).getPrice();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Buy " + subject.game().data().getItem(itemID).getName(), canChoose(subject), new String[]{vendingMachine.getName()});
	}

}
