package Kartoffel.Licht.Rendering.Shapes;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.Model;

public class SLineBox extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3461758693651231509L;

	public SLineBox(Camera cam) {
		this.main(get(cam), new float[] 
				{0, 0, 0, 1, 
				0, 0, 0, 1, 
				0, 0, 0, 1, 
				0, 0, 0, 1, }
		, new int[] {
				0, 1, 2, 3, 0, 2, 1, 3,
				4, 5, 6, 7, 4, 6, 5, 7,
				0, 4, 1, 5, 2, 6, 3, 7
		}, new float[] {0});
		this.drawType = Model.GL_LINES;
	}
	public SLineBox() {
		this.main(get(), new float[] 
				{0, 0, 0, 1, 
				0, 0, 0, 1, 
				0, 0, 0, 1, 
				0, 0, 0, 1, }
		, new int[] {
				0, 1, 2, 3, 0, 2, 1, 3,
				4, 5, 6, 7, 4, 6, 5, 7,
				0, 4, 1, 5, 2, 6, 3, 7
		}, new float[] {0});
		this.drawType = Model.GL_LINES;
	}
	
//	private static final float VIEW_END = 0.5f;
//	private static final float VIEW_START = 0.1f;
	
	static private float[] get(Camera cam) {
		
		Matrix4f projection = cam.getProjection().invert(new Matrix4f());
		
//		float NEAR_PLANE = (1-1/(cam.getProjection().perspectiveFar()*cam.getProjection().perspectiveNear()*VIEW_START));
//		float FAR_PLANE = (1-1/(cam.getProjection().perspectiveFar()*cam.getProjection().perspectiveNear()*VIEW_END));
		
		float[] ver = new float[24];
		Vector4f v = new Vector4f(-1, -1, -1, 1);
		
		v.mulProject(projection);
		ver[0] = v.x;
		ver[1] = v.y;
		ver[2] = v.z;
		
		v.set(1, -1, -1, 1);
		v.mulProject(projection);
		ver[3] = v.x;
		ver[4] = v.y;
		ver[5] = v.z;
		
		v.set(-1, 1, -1, 1);
		v.mulProject(projection);
		ver[6] = v.x;
		ver[7] = v.y;
		ver[8] = v.z;
		
		v.set(1, 1, -1, 1);
		v.mulProject(projection);
		ver[9] = v.x;
		ver[10] = v.y;
		ver[11] = v.z;
		
		v.set(-1, -1, 1, 1);
		v.mulProject(projection);
		ver[12] = v.x;
		ver[13] = v.y;
		ver[14] = v.z;
		
		
		v.set(1, -1, 1, 1);
		v.mulProject(projection);
		ver[15] = v.x;
		ver[16] = v.y;
		ver[17] = v.z;
		
		v.set(-1, 1, 1, 1);
		v.mulProject(projection);
		ver[18] = v.x;
		ver[19] = v.y;
		ver[20] = v.z;
		
		v.set(1, 1, 1, 1);
		v.mulProject(projection);
		ver[21] = v.x;
		ver[22] = v.y;
		ver[23] = v.z;
		
		return ver;
	}
	
	static private float[] get() {
		float[] ver = new float[24];
		ver[0] = -1;
		ver[1] = -1;
		ver[2] = -1;
		ver[3] = 1;
		ver[4] = -1;
		ver[5] = -1;
		ver[6] = -1;
		ver[7] = 1;
		ver[8] = -1;
		ver[9] = 1;
		ver[10] = 1;
		ver[11] = -1;
		ver[12] = -1;
		ver[13] = -1;
		ver[14] = 1;
		ver[15] = 1;
		ver[16] = -1;
		ver[17] = 1;
		ver[18] = -1;
		ver[19] = 1;
		ver[20] = 1;
		ver[21] = 1;
		ver[22] = 1;
		ver[23] = 1;
		return ver;
	}


	
	
}
