package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;

public interface Noun {
	
	public String getName();
	
	public String getFormattedName();
	
	public boolean isProperName();
	
	public Pronoun getPronoun();
	
}
