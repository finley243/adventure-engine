package com.github.finley243.adventureengine.textgen;

import java.util.HashMap;
import java.util.Map;

public class Context {
	
	public enum Pronoun{
		HE("he", "him", "his", "himself", true),
		SHE("she", "her", "her", "herself", true),
		// TODO - Add two variants of THEY pronoun for singular and plural referents (singular uses third person verb when using the name but not the pronoun)
		THEY("they", "them", "their", "themselves", false),
		IT("it", "it", "its", "itself", true),
		I("I", "me", "my", "myself", false),
		WE("we", "us", "our", "ourselves", false),
		YOU("you", "you", "your", "yourself", false),
		YOUALL("you", "you", "your", "yourselves", false);
		
		public final String subject, object, possessive, reflexive;
		public final boolean thirdPersonVerb;
		
		Pronoun(String subject, String object, String possessive, String reflexive, boolean thirdPersonVerb){
			this.subject = subject;
			this.object = object;
			this.possessive = possessive;
			this.reflexive = reflexive;
			this.thirdPersonVerb = thirdPersonVerb;
		}
	}

	private final Map<String, Noun> objects;
	private final Map<String, String> vars;

	public Context(Map<String, Noun> objects) {
		this(new HashMap<>(), objects);
	}

	public Context(Map<String, String> vars, Map<String, Noun> objects) {
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
		return (o instanceof Context) && objects.equals(((Context) o).getObjects());
	}
	
}
