package Kartoffel.Licht.Input;

public class Formatting {
	
	public static final char FONT_CHANGE = 5001;
	public static final char COLOR_CHANGE = 4663;
	public static final char SIZE_CHANGE = 4664;
	public static final char OVERDRAW = 4665;
	public static boolean isSpecial(int cp) {
		return cp == FONT_CHANGE || cp == COLOR_CHANGE || cp == SIZE_CHANGE || cp == OVERDRAW;
	}

}
