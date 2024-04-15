package Kartoffel.Licht.Tools;

import java.util.Random;

public class UUID {
	
	private int value;
	
	public UUID() {
		value = createI();
	}
	
	public UUID(int val) {
		value = val;
	}

	public static int createI() {
		Random r = new Random();
		return r.nextInt();
	}
	
	public static String createS() {
		Random r = new Random();
		return r.nextInt()+"";
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
