package personal.finley.adventure_engine.world;

import personal.finley.adventure_engine.textgen.Context.Pronoun;

public interface INoun {
	
	public String getName();
	
	public String getFormattedName();
	
	public boolean isProperName();
	
	public Pronoun getPronoun();
	
}
