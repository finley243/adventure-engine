package com.github.finley243.adventureengine.menu.data;

public abstract class MenuData {

	private int index;
	private final String prompt;
	private final boolean enabled;
	
	public MenuData(String prompt, boolean enabled) {
		this.prompt = prompt;
		this.enabled = enabled;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getPrompt() {
		return prompt;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
