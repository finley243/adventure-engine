package com.github.finley243.adventureengine.menu.data;

public abstract class MenuData {

	private int index;
	private final String prompt;
	
	public MenuData(String prompt) {
		this.prompt = prompt;
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
	
}
