package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.world.object.ObjectVendingMachine;
import com.github.finley243.adventureengine.item.ItemFactory;

public class ActionVendingMachineBuy extends Action {

	private final ObjectVendingMachine vendingMachine;
	private final String itemID;
	
	public ActionVendingMachineBuy(ObjectVendingMachine vendingMachine, String itemID) {
		super(ActionDetectionChance.LOW);
		this.vendingMachine = vendingMachine;
		this.itemID = itemID;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Item item = ItemFactory.create(subject.game(), itemID);
		subject.adjustMoney(-item.getTemplate().getPrice());
		subject.inventory().addItem(item);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).put("vendor", vendingMachine).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("buy"), context, this, null, subject, null));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && subject.getMoney() >= subject.game().data().getItem(itemID).getPrice();
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice("Buy " + subject.game().data().getItem(itemID).getName(), canChoose(subject), new String[]{vendingMachine.getName()}, new String[]{"buy " + subject.game().data().getItem(itemID).getName() + " from " + vendingMachine.getName()});
	}

}
