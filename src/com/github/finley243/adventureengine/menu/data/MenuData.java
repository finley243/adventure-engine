package com.github.finley243.adventureengine.menu.data;

public abstract class MenuData {

	private String prompt;
	
	public MenuData(String prompt) {
		this.prompt = prompt;
	}
	
	public String getPrompt() {
		return prompt;
	}
	
}
