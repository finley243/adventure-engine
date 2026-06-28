package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;

import java.util.List;

public class ActionItemConsume extends Action {

	private final Item item;
	private final String consumePrompt;
	private final String consumePhrase;
	private final List<Effect> effects;
	
	public ActionItemConsume(Actor subject, ActionDependencies dependencies, Item item, String consumePrompt, String consumePhrase, List<Effect> effects) {
        super(subject, dependencies);
        this.item = item;
		this.consumePrompt = consumePrompt;
		this.consumePhrase = consumePhrase;
		this.effects = effects;
	}

	@Override
	public String getID() {
		return "item_consume";
	}

	@Override
	public Context getContext() {
        return Context.builder().subject(subject).parentItem(item).build();
	}
	
	@Override
	public void choose(int repeatActionCount) {
		subject.getInventory().removeItem(item);
		Context context = getContext();
		sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), consumePhrase, context, true, this, null));
		for (Effect effect : effects) {
			subject.getEffectComponent().addEffect(effect);
		}
	}

	@Override
	public MenuData getMenuData() {
		return new MenuDataInventory(item, subject.getInventory());
	}

	@Override
	public String getPrompt() {
		return consumePrompt;
	}

	@Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionItemConsume other)) {
            return false;
        } else {
			return other.item == this.item;
        }
    }

}
