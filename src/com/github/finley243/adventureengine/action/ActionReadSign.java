package com.github.finley243.adventureengine.action;

import java.util.List;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorPlayer;
import com.github.finley243.adventureengine.event.RenderTextEvent;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.object.ObjectSign;

public class ActionReadSign implements Action {

	private ObjectSign sign;
	
	public ActionReadSign(ObjectSign sign) {
		this.sign = sign;
	}
	
	@Override
	public void choose(Actor subject) {
		Context context = new Context(subject, sign);
		Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("read"), context));
		if(subject instanceof ActorPlayer) {
			List<String> text = sign.getText();
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
			for(String line : text) {
				Game.EVENT_BUS.post(new RenderTextEvent(line));
			}
			Game.EVENT_BUS.post(new RenderTextEvent("-----------"));
		}
	}

	@Override
	public String getPrompt() {
		return "Read " + sign.getFormattedName();
	}

	@Override
	public float utility(Actor subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String[] getMenuStructure() {
		return new String[] {"World", LangUtils.capitalize(sign.getName())};
	}

}
