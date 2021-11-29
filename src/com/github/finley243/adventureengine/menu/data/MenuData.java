package com.github.finley243.adventureengine.menu.data;

public abstract class MenuData implements Comparable<MenuData> {

	private int index;
	private final String prompt;
	private final String fullPrompt;
	private final boolean enabled;
	
	public MenuData(String prompt, String fullPrompt, boolean enabled) {
		this.prompt = prompt;
		this.fullPrompt = fullPrompt;
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

	public String getFullPrompt() {
		return fullPrompt;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public int compareTo(MenuData other) {
		return Integer.compare(this.getIndex(), other.getIndex());
	}

}
