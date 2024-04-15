package Kartoffel.Licht.Rendering.Shapes;

public class SPBox2D extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7294662353616569219L;

	public SPBox2D() {
		this.main(
		//####################################
		new float[] {
		-0.5f, 0.5f, -1f,	//Top Left
		 0.5f, 0.5f, -1f,	//top Right
		 0.5f, -0.5f, -1f,	//Bottom Right
		 -0.5f, -0.5f, -1f,	//Bottom Left
		},
		new float[] {
				0,1,
				1,1,
				1,0,
				0,0
		},
		new int[] {
				0, 1, 2,
				2, 3, 1
		}
		//#####################################
, null
		);
	}
	/**
	 * sx & sy are relative to the rendering box (init Window size). for a object that reaches
	 * to the end of the rendering box just type in 1.
	 * 
	 * @param sx
	 * @param sy
	 */
	public SPBox2D(float sx, float sy) {
		this.main(
		//####################################
		new float[] {
		-sx, sy, -1f,	//Top Left
		 sx, sy, -1f,	//top Right
		 sx, -sy, -1f,	//Bottom Right
	    -sx, -sy, -1f,	//Bottom Left
		},
		new float[] {
				0,1,
				1,1,
				1,0,
				0,0
		},
		new int[] {
				0, 2, 1,
				2, 0, 3
		}
		//#####################################
, null
		);
	}
	
	public SPBox2D(float sx, float sy, float px, float py) {
		this.main(
		//####################################
		new float[] {
		-sx+px, sy+py, -1f,	//Top Left
		 sx+px, sy+py, -1f,	//top Right
		 sx+px, -sy+py, -1f,	//Bottom Right
	    -sx+px, -sy+py, -1f,	//Bottom Left
		},
		new float[] {
				0,1,
				1,1,
				1,0,
				0,0
		},
		new int[] {
				0, 1, 2,
				2, 3, 0
		}
		//#####################################
, null
		);
	}

}
