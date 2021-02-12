package personal.finley.adventure_engine_2.world.template;

public class StatsConsumable {

	public enum ConsumableType{
		FOOD, DRINK, OTHER
	}
	
	private String name;
	private ConsumableType type;
	
	public StatsConsumable() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public ConsumableType getType() {
		return type;
	}
	
}
