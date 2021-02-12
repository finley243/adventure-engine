package personal.finley.adventure_engine_2.event;

import personal.finley.adventure_engine_2.textgen.Context;
import personal.finley.adventure_engine_2.textgen.Phrases;

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
