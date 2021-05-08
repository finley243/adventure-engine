package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectChair;

public class ActionSit implements Action {

	private ObjectChair chair;
	
	public ActionSit(ObjectChair chair) {
		this.chair = chair;
	}
	
	@Override
	public void choose(Actor subject) {
		chair.setUser(subject);
		subject.startUsingObject(chair);
		Context context = new Context(subject, chair);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("sit"), context));
	}

	@Override
	public String getPrompt() {
		return "Sit in " + chair.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.capitalize(chair.getName())};
	}

}
