package com.github.finley243.adventureengine.quest;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;

public class Objective {

	private boolean isEnabled;
	private final String prompt;
	
	public Objective(String prompt) {
		this.isEnabled = false;
		this.prompt = prompt;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setEnabled(boolean state) {
		if(state) {
			isEnabled = true;
			Game.EVENT_BUS.post(new RenderTextEvent("Objective - " + prompt));
		} else {
			isEnabled = false;
		}
	}

}
