package personal.finley.adventure_engine_2;

public class EnumTypes {
	
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
	
	// ------------------------------------------------
	
	public enum WeaponType{
		PISTOL(true, false, 2),
		SMG(true, false, 2),
		SHOTGUN(true, true, 1),
		ASSAULT_RIFLE(true, true, 2),
		SNIPER_RIFLE(true, true, 1),
		KNIFE(false, false, 2),
		SWORD(false, true, 1),
		CLUB(false, true, 1),
		AXE(false, true, 1);
		
		public final boolean isRanged, isTwoHanded;
		public final int rate;
		
		WeaponType(boolean isRanged, boolean isTwoHanded, int rate) {
			this.isRanged = isRanged;
			this.isTwoHanded = isTwoHanded;
			this.rate = rate;
		}
	}
	
	public enum ApparelType{
		BODY, FACE, HEAD, FEET, ARMS, LEGS
	}
	
	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	// ------------------------------------------------
	
	public enum Skill{
		BODY, INTELLIGENCE, CHARISMA, DEXTERITY, AGILITY
	}
	
}
