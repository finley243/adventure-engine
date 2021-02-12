package personal.finley.adventure_engine_2.world;

import personal.finley.adventure_engine_2.textgen.Context.Pronoun;

public interface INoun {
	
	public String getName();
	
	public String getFormattedName();
	
	public boolean isProperName();
	
	public Pronoun getPronoun();
	
}
