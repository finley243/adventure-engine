package personal.finley.adventure_engine.event;

import personal.finley.adventure_engine.textgen.Context;
import personal.finley.adventure_engine.textgen.Phrases;

public class TextEvent {

	private Context context;
	private String text;
	
	public TextEvent(Context context, String textID) {
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
