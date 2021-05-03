package personal.finley.adventure_engine.textgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Phrases {

	public static final String PHRASE_FILE_LOCATION = "src/gamefiles/phrases.txt";
	
	private static Map<String, String> phrases = new HashMap<String, String>();
	
	public static void load() throws FileNotFoundException {
		File file = new File(PHRASE_FILE_LOCATION);
		Scanner scanner = new Scanner(file);
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] split = line.split(":");
			if(split.length != 2) {
				System.out.println("ERROR - INVALID PHRASE FILE FORMAT - LINE: " + line);
			}
			phrases.put(split[0].trim(), split[1].trim());
		}
		scanner.close();
	}
	
	public static String get(String ID) {
		return phrases.get(ID);
	}
	
}
