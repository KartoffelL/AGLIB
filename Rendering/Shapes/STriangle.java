package Kartoffel.Licht.Rendering.Shapes;
/**
 * Dont ask.
 */
public class STriangle extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2763728154461400215L;

	/**
	 * Dont ask.
	 */
	public STriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
		this.main(new float[] {x1, y1, z1,  x2, y2, z2,  x3, y3, z3}, new float[] {0, 0,  1, 1,  0, 0}, new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, new float[] {0, 0, 0,  0, 0, 0,  0, 0, 0});
		
	}

}
