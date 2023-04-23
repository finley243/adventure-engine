package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.textgen.TextContext.Pronoun;

/**
 * Represents anything that can be referred to as a noun (used for text generation)
 */
public interface Noun {
	
	String getName();
	
	String getFormattedName();

	void setKnown();

	boolean isKnown();
	
	boolean isProperName();
	
	Pronoun getPronoun();

	boolean forcePronoun();

}
