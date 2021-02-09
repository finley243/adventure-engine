package personal.finley.adventure_engine_2.dialogue;

import java.util.List;

import personal.finley.adventure_engine_2.Condition;

public class Topic {

	private boolean exit;
	
	private String prompt;
	private String response;
	private List<String> choices;
	
	private Condition condition;
	
	public Topic(String prompt, String response, List<String> choices, boolean exit) {
		this.prompt = prompt;
		this.response = response;
		this.choices = choices;
		this.exit = exit;
	}
	
	public boolean exit() {
		return exit;
	}
	
	public String getPrompt() {
		return prompt;
	}
	
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public String getResponse() {
		return response;
	}
	
	public List<String> getChoices(){
		return choices;
	}
	
	public boolean canChoose() {
		if(condition == null) {
			return true;
		}
		return condition.isMet();
	}
	
}
