package com.github.finley243.adventureengine.textgen;

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
	
	public enum Benefitting{
		SUBJECT, OBJECT
	}

	private final Noun subject;
	private final Noun object;
	private final Noun object2;
	
	public Context(Noun subject) {
		this(subject, subject, subject);
	}
	
	public Context(Noun subject, Noun object) {
		this(subject, object, object);
	}
	
	public Context(Noun subject, Noun object, Noun object2) {
		if(subject == null || object == null || object2 == null) throw new IllegalArgumentException("Context arguments cannot be null");
		this.subject = subject;
		this.object = object;
		this.object2 = object2;
	}

	public Noun getSubject() {
		return subject;
	}

	public Noun getObject() {
		return object;
	}

	public Noun getObject2() {
		return object2;
	}
	
}
