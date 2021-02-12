package personal.finley.adventure_engine_2.world.template;

public class TemplateConsumable {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private String name;
	private ConsumableType type;
	
	public TemplateConsumable() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
}
