package Kartoffel.Licht.Rendering.Shapes;

import Kartoffel.Licht.Rendering.Model;

public class SPoint extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = 437587571964057690L;

	public SPoint() {
		this.main(
				//####################################
				new float[] {
						0, 0, 0,	//Top Left
				},
				new float[] {
						0,0,
				},
				new int[] {
						0
				}
				//#####################################
				, null
				);
		this.drawType = Model.GL_POINTS;
	}

}
