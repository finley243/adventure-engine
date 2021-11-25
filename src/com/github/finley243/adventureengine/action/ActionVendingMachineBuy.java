package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldObject;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ActionVendingMachineBuy implements Action {

	private boolean disabled;
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
	public void disable() {
		disabled = true;
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
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return true;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		return false;
	}
	
	@Override
	public int actionCount() {
		return 1;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldObject("Buy " + Data.getItem(itemID).getName(), canChoose(subject), vendingMachine);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionVendingMachineBuy)) {
            return false;
        } else {
            ActionVendingMachineBuy other = (ActionVendingMachineBuy) o;
            return other.vendingMachine == this.vendingMachine && other.itemID.equals(this.itemID);
        }
    }

}
