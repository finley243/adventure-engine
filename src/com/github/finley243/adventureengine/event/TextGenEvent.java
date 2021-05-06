package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class TextGenEvent {

	private Context context;
	private String text;
	
	public TextGenEvent(Context context, String textID) {
		this.context = context;
		this.text = Phrases.get(textID);
	}
	
	public Context getContext() {
		return context;
	}
	
	public String getText() {
		return text;
	}
	
}
