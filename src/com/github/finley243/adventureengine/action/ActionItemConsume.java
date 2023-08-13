package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.item.ItemConsumable;

public class ActionItemConsume extends Action {

	private final ItemConsumable item;
	
	public ActionItemConsume(ItemConsumable item) {
		this.item = item;
	}
	
	@Override
	public void choose(Actor subject, int repeatActionCount) {
		subject.getInventory().removeItem(item);
		TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).put("item", item).build());
		String phrase = item.getConsumePhrase();
		subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(phrase), context, this, null, subject, null));
		for (String effect : item.getEffects()) {
			subject.getEffectComponent().addEffect(effect);
		}
		subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		String prompt = item.getConsumePrompt();
		return new MenuChoice(prompt, canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"consume " + item.getName(), "use " + item.getName(), prompt.toLowerCase() + " " + item.getName()});
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
