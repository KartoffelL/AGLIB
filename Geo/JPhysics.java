package Kartoffel.Licht.Geo;

public class JPhysics {

	public static boolean intersectionLineSegmentPoint(float x1, float y1, float x2, float y2, float px, float py, float bias) {
		float f = (x1+x2)/2;
		float d = (y1+y2)/2;
		float a = (y1-y2)/(x1-x2);
		float c = sqr((y1-y2)/2)+sqr((x1-x2)/2);
		float g = (px-f)*a+d;
		if(sqr(px-f)+sqr(px*a-f*a) < c) { //Check if in between point segments
			if(g < py+bias && g > py-bias) //If in bias
				return true;
		}
		return false;
	}
	public static boolean intersectionLinePoint(float x1, float y1, float x2, float y2, float px, float py, float bias) {
		double d = Math.abs((x2-x1)*(y1-py)-(x1-px)*(y2-y1))/Math.sqrt(sqr(x2-x1)+sqr(y2-y1));
		double l1 = (x1-x2)*(px-x2)+(y1-y2)*(py-y2);
		double l2 = (x1-x2)*(px-x1)+(y1-y2)*(py-y1);
		return d < bias && l1 > 0 && l2 < 0;
	}
	private static float sqr(float a) {
		return a*a;
	}

}
