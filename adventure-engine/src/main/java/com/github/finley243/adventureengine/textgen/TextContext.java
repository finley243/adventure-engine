package com.github.finley243.adventureengine.textgen;

import java.util.Map;

public class TextContext {
	
	public enum Pronoun{
		HE("he", "him", "his", "himself", true, false),
		SHE("she", "her", "her", "herself", true, false),
		THEY("they", "them", "their", "themselves", false, false),
		IT("it", "it", "its", "itself", true, false),
		I("I", "me", "my", "myself", false, true),
		WE("we", "us", "our", "ourselves", false, true),
		YOU("you", "you", "your", "yourself", false, true),
		YOUALL("you", "you", "your", "yourselves", false, true);
		
		public final String subject, object, possessive, reflexive;
		public final boolean thirdPersonVerb, forcePronoun;
		
		Pronoun(String subject, String object, String possessive, String reflexive, boolean thirdPersonVerb, boolean forcePronoun){
			this.subject = subject;
			this.object = object;
			this.possessive = possessive;
			this.reflexive = reflexive;
			this.thirdPersonVerb = thirdPersonVerb;
			this.forcePronoun = forcePronoun;
		}
	}

	private final Map<String, Noun> objects;
	private final Map<String, String> vars;

	public TextContext(Map<String, String> vars, Map<String, Noun> objects) {
		if (objects == null) throw new IllegalArgumentException("Context objects cannot be null");
		for (Noun object : objects.values()) {
			if(object == null) throw new IllegalArgumentException("Context objects cannot be null");
		}
		if (vars == null) throw new IllegalArgumentException("Context vars cannot be null");
		this.objects = objects;
		this.vars = vars;
	}

	public Map<String, Noun> getObjects() {
		return objects;
	}

	public Map<String, String> getVars() {
		return vars;
	}

	@Override
	public boolean equals(Object o) {
		// Not checking for vars equality
		return (o instanceof TextContext) && objects.equals(((TextContext) o).getObjects());
	}
	
}
