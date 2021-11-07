package com.github.finley243.adventureengine.textgen;

import com.github.finley243.adventureengine.world.Noun;

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
	
	private final boolean indefiniteSubject;
	private final boolean indefiniteObject;
	private final boolean indefiniteObject2;
	
	public Context(Noun subject, boolean indefiniteSubject) {
		this(subject, indefiniteSubject, subject, indefiniteSubject, subject, indefiniteSubject);
	}
	
	public Context(Noun subject, boolean indefiniteSubject, Noun object, boolean indefiniteObject) {
		this(subject, indefiniteSubject, object, indefiniteObject, object, indefiniteObject);
	}
	
	public Context(Noun subject, boolean indefiniteSubject, Noun object, boolean indefiniteObject, Noun object2, boolean indefiniteObject2) {
		this.subject = subject;
		this.object = object;
		this.object2 = object2;
		this.indefiniteSubject = indefiniteSubject;
		this.indefiniteObject = indefiniteObject;
		this.indefiniteObject2 = indefiniteObject2;
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
	
	public boolean indefiniteSubject() {
		return indefiniteSubject;
	}
	
	public boolean indefiniteObject() {
		return indefiniteObject;
	}
	
	public boolean indefiniteObject2() {
		return indefiniteObject2;
	}
	
}
