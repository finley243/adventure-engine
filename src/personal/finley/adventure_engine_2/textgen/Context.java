package personal.finley.adventure_engine_2.textgen;

import personal.finley.adventure_engine_2.world.INoun;

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

	private INoun subject;
	private INoun object;
	private INoun object2;
	
	private Benefitting benefitting;
	
	public final boolean isCompleteSentence;
	public final boolean isEndClause;
	
	public Context(INoun subject, Benefitting benefitting, boolean isCompleteSentence, boolean isEndClause) {
		this(subject, subject, subject, benefitting, isCompleteSentence, isEndClause);
	}
	
	public Context(INoun subject, INoun object, Benefitting benefitting, boolean isCompleteSentence, boolean isEndClause) {
		this(subject, object, object, benefitting, isCompleteSentence, isEndClause);
	}
	
	public Context(INoun subject, INoun object, INoun object2, Benefitting benefitting, boolean isCompleteSentence, boolean isEndClause) {
		this.subject = subject;
		this.object = object;
		this.object2 = object2;
		this.isCompleteSentence = isCompleteSentence;
		this.isEndClause = isEndClause;
	}

	public INoun getSubject() {
		return subject;
	}

	public INoun getObject() {
		return object;
	}

	public INoun getObject2() {
		return object2;
	}
	
	public Benefitting getBenefitting() {
		return benefitting;
	}
	
}
