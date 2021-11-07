package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.textgen.Context.Pronoun;

/**
 * Represents anything that can be referred to as a noun (used for text generation)
 */
public interface Noun {
	
	String getName();
	
	String getFormattedName(boolean indefinite);
	
	boolean isProperName();
	
	Pronoun getPronoun();
	
}
