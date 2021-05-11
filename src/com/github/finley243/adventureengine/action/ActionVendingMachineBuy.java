package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
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
		subject.adjustMoney(-item.getPrice());
		subject.inventory().addItem(item);
		Context context = new Context(subject, false, item, true, vendingMachine, false);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("buyFrom"), context));
	}

	@Override
	public String getPrompt() {
		return "Buy " + Data.getItem(itemID).getFormattedName(true) + " from " + vendingMachine.getFormattedName(false) + " [" + Data.getItem(itemID).getPrice() + " credits]";
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int actionPoints() {
		return 1;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.titleCase(vendingMachine.getName())};
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.LEGAL;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataWorldObject("Buy " + Data.getItem(itemID).getName(), vendingMachine);
	}

}
