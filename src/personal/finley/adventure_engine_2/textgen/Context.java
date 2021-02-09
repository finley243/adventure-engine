package personal.finley.adventure_engine_2.textgen;

import personal.finley.adventure_engine_2.EnumTypes.Benefitting;
import personal.finley.adventure_engine_2.world.INoun;

public class Context {

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
