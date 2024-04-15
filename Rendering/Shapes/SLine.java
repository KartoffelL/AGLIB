package Kartoffel.Licht.Rendering.Shapes;

import org.joml.Vector3f;

import Kartoffel.Licht.Rendering.Model;

public class SLine extends Shape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8516530966870759359L;

	public SLine(Vector3f p1, Vector3f p2) {
		this.main(
				//####################################
				new float[] {
						p1.x, p1.y, p1.z,	//Top Left
						p2.x, p2.y, p2.z,	//top Right
				},
				new float[] {
						0,0,
						0,1,
				},
				new int[] {
						0, 1
				}
				//#####################################
, null
				);
		this.drawType = Model.GL_LINES;
	}
	
	public SLine(float a_x, float a_y, float a_z, float b_x, float b_y, float b_z) {
		this.main(
				//####################################
				new float[] {
						a_x, a_y, a_z,	//Top Left
						b_x, b_y, b_z,	//top Right
				},
				new float[] {
						0,0,
						0,1,
				},
				new int[] {
						0, 1
				}
				//#####################################
, null
				);
		this.drawType = Model.GL_LINES;
	}

}
