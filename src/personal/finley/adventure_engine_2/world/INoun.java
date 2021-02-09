package personal.finley.adventure_engine_2.world;

import personal.finley.adventure_engine_2.EnumTypes.Pronoun;

public interface INoun {
	
	public String getName();
	
	public boolean isProperName();
	
	public Pronoun getPronoun();
	
}
