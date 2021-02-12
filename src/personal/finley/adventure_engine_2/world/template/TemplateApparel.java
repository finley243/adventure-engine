package personal.finley.adventure_engine_2.world.template;

public class TemplateApparel {

	public enum ApparelType{
		BODY, FACE, HEAD, FEET, ARMS, LEGS
	}
	
	private String name;
	private ApparelType type;
	
	public TemplateApparel() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public ApparelType getType() {
		return type;
	}
	
}
