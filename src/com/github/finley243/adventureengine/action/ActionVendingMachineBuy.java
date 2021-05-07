package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Context.Benefitting;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ActionVendingMachineBuy implements Action {

	private ObjectVendingMachine vendingMachine;
	private String itemID;
	
	public ActionVendingMachineBuy(ObjectVendingMachine vendingMachine, String itemID) {
		this.vendingMachine = vendingMachine;
		this.itemID = itemID;
	}
	
	@Override
	public void choose(Actor subject) {
		Item item = ItemFactory.create(itemID);
		subject.addMoney(-item.getPrice());
		subject.inventory().addItem(item);
		Context context = new Context(subject, item, vendingMachine, Benefitting.SUBJECT, false, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("vendingMachineBuy"), context));
	}

	@Override
	public String getChoiceName() {
		return "Buy " + Data.getItem(itemID).getFormattedName() + " from " + vendingMachine.getFormattedName() + " [" + Data.getItem(itemID).getPrice() + " credits]";
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}

}