package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.item.ItemNote;

import java.util.List;

public class ActionReadNote extends Action {

	private final ItemNote note;

	public ActionReadNote(ItemNote note) {
		this.note = note;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, note);
		subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("read"), context, this, subject));
		if(subject instanceof ActorPlayer) {
			List<String> text = note.getText();
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
		return new MenuData("Read", canChoose(subject), new String[]{"inventory", note.getName()});
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionReadNote)) {
            return false;
        } else {
            ActionReadNote other = (ActionReadNote) o;
            return other.note == this.note;
        }
    }

}
