package com.github.finley243.adventureengine.menu;

import java.util.Scanner;

public class UserInput {

	private static final Scanner CONSOLE = new Scanner(System.in);
	
	public static int intInRange(int min, int max) {
		int input;
		do {
			input = CONSOLE.nextInt();
			if(input > max || input < min) {
				System.out.println("Input outside range. Please enter a valid number.");
			}
		} while(input > max || input < min);
		return input;
	}
	
}
