package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.item.template.ItemTemplate;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.component.ObjectComponentVending;

public class ActionObjectVendingBuy extends Action {

	private final ObjectComponentVending component;
	private final String itemID;

	public ActionObjectVendingBuy(ObjectComponentVending component, String itemID) {
		super(ActionDetectionChance.LOW);
		this.component = component;
		this.itemID = itemID;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		Item item = ItemFactory.create(subject.game(), itemID);
		subject.adjustMoney(-item.getTemplate().getPrice());
		subject.getInventory().addItem(item);
		Context context = new Context(new NounMapper().put("actor", subject).put("item", item).put("vendor", component.getObject()).build());
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("buy"), context, this, null, subject, null));
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && subject.getMoney() >= subject.game().data().getItem(itemID).getPrice();
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		ItemTemplate item = subject.game().data().getItem(itemID);
		return new MenuChoice("Buy " + item.getName() + " (" + item.getPrice() + ")", canChoose(subject), new String[]{component.getObject().getName()}, new String[]{"buy " + subject.game().data().getItem(itemID).getName() + " from " + component.getObject().getName()});
	}

}
