package personal.finley.adventure_engine_2.world.template;

import personal.finley.adventure_engine_2.EnumTypes.Pronoun;

public class TemplateActor {

	private String name;
	private boolean isProperName;
	private Pronoun pronoun;
	
	public TemplateActor(String name, boolean isProperName, Pronoun pronoun) {
		this.name = name;
		this.isProperName = isProperName;
		this.pronoun = pronoun;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isProperName() {
		return isProperName;
	}
	
	public Pronoun getPronoun() {
		return pronoun;
	}
	
}
