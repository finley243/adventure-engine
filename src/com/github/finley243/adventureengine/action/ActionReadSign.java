package com.github.finley243.adventureengine.action;

import java.util.List;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectSign;

public class ActionReadSign extends Action {

	private final ObjectSign sign;
	private final boolean isInInventory;
	
	public ActionReadSign(ObjectSign sign, boolean isInInventory) {
		this.sign = sign;
		this.isInInventory = isInInventory;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, sign);
		subject.game().eventBus().post(new VisualEvent(subject.getArea(), Phrases.get("read"), context, this, subject));
		if(subject instanceof ActorPlayer) {
			List<String> text = sign.getText();
			subject.game().eventBus().post(new RenderTextEvent("-----------"));
			for(String line : text) {
				subject.game().eventBus().post(new RenderTextEvent(line));
			}
			subject.game().eventBus().post(new RenderTextEvent("-----------"));
		}
	}

	@Override
	public int actionPoints(Actor subject) {
		return 0;
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		String[] category;
		if(isInInventory) {
			category = new String[]{"inventory", sign.getName()};
		} else {
			category = new String[]{sign.getName()};
		}
		return new MenuData("Read", canChoose(subject), category);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionReadSign)) {
            return false;
        } else {
            ActionReadSign other = (ActionReadSign) o;
            return other.sign == this.sign;
        }
    }

}
