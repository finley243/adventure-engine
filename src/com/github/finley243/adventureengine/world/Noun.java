package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;

/**
 * Represents anything that can be referred to as a noun (used for text generation)
 */
public interface Noun {
	
	public String getName();
	
	public String getFormattedName(boolean indefinite);
	
	public boolean isProperName();
	
	public Pronoun getPronoun();
	
}
