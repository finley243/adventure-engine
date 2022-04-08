package com.github.finley243.adventureengine.textgen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Context {
	
	public enum Pronoun{
		HE("he", "him", "his", "himself", true),
		SHE("she", "her", "her", "herself", true),
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

	private final Noun subject;
	private final Noun[] objects;
	private final Map<String, String> vars;

	public Context(Noun subject, Noun... objects) {
		this(new HashMap<>(), subject, objects);
	}

	public Context(Map<String, String> vars, Noun subject, Noun... objects) {
		if(subject == null) throw new IllegalArgumentException("Context subject cannot be null");
		if(objects == null) throw new IllegalArgumentException("Context objects cannot be null");
		for(Noun object : objects) {
			if(object == null) throw new IllegalArgumentException("Context objects cannot be null");
		}
		if(vars == null) throw new IllegalArgumentException("Context vars cannot be null");
		this.subject = subject;
		this.objects = objects;
		this.vars = vars;
	}

	public Noun getSubject() {
		return subject;
	}

	public Noun[] getObjects() {
		return objects;
	}

	public Map<String, String> getVars() {
		return vars;
	}

	@Override
	public boolean equals(Object o) {
		// Not checking for vars equality
		return (o instanceof Context) && subject.equals(((Context) o).getSubject()) && Arrays.equals(objects, ((Context) o).getObjects());
	}
	
}
