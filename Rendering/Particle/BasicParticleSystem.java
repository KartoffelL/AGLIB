package Kartoffel.Licht.Rendering.Particle;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import Kartoffel.Licht.Geo.DPoint;
import Kartoffel.Licht.Geo.DefinableShape;
import Kartoffel.Licht.Geo.translate;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.InstancedModel;
import Kartoffel.Licht.Rendering.Shapes.ShapeData;
import Kartoffel.Licht.Rendering.Texture.Renderable;


/**
 * Tool for displaying/handling instanced Models
 *
 */
public class BasicParticleSystem extends GEntity{
	
	private float[][][] matrices;
	private float[][][] motion;
	private DefinableShape resetShape = new DPoint();
	private double[] live;
	private double max_live = 5;
	
	private static Random random = new Random();
	
	
	public BasicParticleSystem(Renderable t, ShapeData shape) {
		super(t, new InstancedModel(shape, 500, InstancedModel.ADT_TRANSMATRIX));
		 matrices = new float[500][3][3];
		 live = new double[500];
		 distribute();
		 motion = new float[500][3][3];
	}
	
	public void update(DefinableShape shape) {
		Matrix4f mat = new Matrix4f();
		Random random = new Random();
		for(int i = 0; i < matrices.length; i++) {
			mat.identity();
			Vector3d d = shape.randomPoint(random, new Vector3d());
			matrices[i][0][0] = (float) d.x;
			matrices[i][0][1] = (float) d.y;
			matrices[i][0][2] = (float) d.z;
			matrices[i][2][0] = 1;
			matrices[i][2][1] = 1;
			matrices[i][2][2] = 1;
			mat.translate((float)d.x, (float)d.y, (float)d.z);
			((InstancedModel)this.getMod()).setInstanceAttribMatrix(i, 0, mat);
		}
	}
	
	public void update(float mx, float my, float mz, float rmx, float rmy, float rmz, float smx, float smy, float smz, float rx, float ry, float rz, float rrx, float rry, float rrz, float rsx, float rsy, float rsz, float rlife, float delta) {
		Matrix4f mat = new Matrix4f();
		for(int i = 0; i < matrices.length; i++) {
			mat.identity();
			
			matrices[i][0][0] += motion[i][0][0];
			matrices[i][0][1] += motion[i][0][1];
			matrices[i][0][2] += motion[i][0][2];
			
			matrices[i][1][0] += motion[i][1][0];
			matrices[i][1][1] += motion[i][1][1];
			matrices[i][1][2] += motion[i][1][2];
			
			matrices[i][2][0] += motion[i][2][0];
			matrices[i][2][1] += motion[i][2][1];
			matrices[i][2][2] += motion[i][2][2];
			
			live[i] += (1+(rlife != 0 ? random.nextDouble(rlife) : 0))*delta;
			
			if(live[i] > max_live) {
				live[i] = 0;
				Vector3d d = resetShape.randomPoint(random, new Vector3d());
				matrices[i][0][0] = (float) d.x;
				matrices[i][0][1] = (float) d.y;
				matrices[i][0][2] = (float) d.z;
				matrices[i][2][0] = 1;
				matrices[i][2][1] = 1;
				matrices[i][2][2] = 1;
				matrices[i][1][0] = 0;
				matrices[i][1][1] = 0;
				matrices[i][1][2] = 0;
				motion[i][0][0] = (float)(mx*(rx != 0 ? random.nextDouble(rx)-rx/2 : 1))*delta;
				motion[i][0][1] = (float)(my*(ry != 0 ? random.nextDouble(ry)-ry/2 : 1))*delta;
				motion[i][0][2] = (float)(mz*(rz != 0 ? random.nextDouble(rz)-rz/2 : 1))*delta;
				motion[i][1][0] = (float)(rmx*(rrx != 0 ? random.nextDouble(rrx)-rrx/2 : 1))*delta;
				motion[i][1][1] = (float)(rmy*(rry != 0 ? random.nextDouble(rry)-rry/2 : 1))*delta;
				motion[i][1][2] = (float)(rmz*(rrz != 0 ? random.nextDouble(rrz)-rrz/2 : 1))*delta;
				motion[i][2][0] = (float)(smx*(rsx != 0 ? random.nextDouble(rsx)-rsx/2 : 1))*delta;
				motion[i][2][1] = (float)(smy*(rsy != 0 ? random.nextDouble(rsy)-rsy/2 : 1))*delta;
				motion[i][2][2] = (float)(smz*(rsz != 0 ? random.nextDouble(rsz)-rsz/2 : 1))*delta;
			}
			
			mat.translate(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2]);
			mat.rotateXYZ(matrices[i][1][0], matrices[i][1][1], matrices[i][1][2]);
			mat.scale(matrices[i][2][0], matrices[i][2][1], matrices[i][2][2]);
			((InstancedModel)this.getMod()).setInstanceAttribMatrix(i, 0, mat);
		}
	}
	
	public void setParticleCount(int c) {
		((InstancedModel)this.getMod()).setMaxInstances(c);
		matrices = new float[c][3][3];
		live = new double[c];
		motion = new float[c][3][3];
	}
	public int getParticleCount() {
		return live.length;
	}
	
	public void setResetShape(DefinableShape resetShape) {
		this.resetShape = resetShape;
	}
	
	public DefinableShape getResetShape() {
		return resetShape;
	}
	
	public void applyPosition(translate t) {
		Matrix4f mat = new Matrix4f();
		for(int i = 0; i < matrices.length; i++) {
			Vector3f f = t.m(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2], 0);
			matrices[i][0][0] = f.x;
			matrices[i][0][1] = f.y;
			matrices[i][0][2] = f.z;
			mat.identity();
			mat.translate(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2]);
			mat.rotateXYZ(matrices[i][1][0], matrices[i][1][1], matrices[i][1][2]);
			mat.scale(matrices[i][2][0], matrices[i][2][1], matrices[i][2][2]);
			((InstancedModel)this.getMod()).setInstanceAttribMatrix(i, 0, mat);
		}
	}
	
	public void applyScale(translate t) {
		Matrix4f mat = new Matrix4f();
		for(int i = 0; i < matrices.length; i++) {
			Vector3f f = t.m(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2], 2);
			matrices[i][2][0] = f.x;
			matrices[i][2][1] = f.y;
			matrices[i][2][2] = f.z;
			mat.identity();
			mat.translate(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2]);
			mat.rotateXYZ(matrices[i][1][0], matrices[i][1][1], matrices[i][1][2]);
			mat.scale(matrices[i][2][0], matrices[i][2][1], matrices[i][2][2]);
			((InstancedModel)this.getMod()).setInstanceAttribMatrix(i, 0, mat);
		}
	}
	
	public void applyRotation(translate t) {
		Matrix4f mat = new Matrix4f();
		for(int i = 0; i < matrices.length; i++) {
			Vector3f f = t.m(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2], 1);
			matrices[i][1][0] = f.x;
			matrices[i][1][1] = f.y;
			matrices[i][1][2] = f.z;
			mat.identity();
			mat.translate(matrices[i][0][0], matrices[i][0][1], matrices[i][0][2]);
			mat.rotateXYZ(matrices[i][1][0], matrices[i][1][1], matrices[i][1][2]);
			mat.scale(matrices[i][2][0], matrices[i][2][1], matrices[i][2][2]);
			((InstancedModel)this.getMod()).setInstanceAttribMatrix(i, 0, mat);
		}
	}
	
	public InstancedModel getMod() {
		return (InstancedModel)super.getMod();
	}
	
	/**
	 * Updates all the data and finalizes the Model. It is then no longer possible to change the data<br>
	 * 
	 */
	public void set() {
		((InstancedModel)this.getMod()).set();
		motion = null;
		matrices = null;
		live = null;
	}
	public void uploadAllData() {
		((InstancedModel)this.getMod()).updateAllData();
	}
	
	public void distribute() {
		Random r = new Random();
		for(int i = 0; i < live.length; i++) 
			live[i] = r.nextDouble(max_live);
	}
	
	/**
	 * In Seconds
	 * @return
	 */
	public double getMax_live() {
		return max_live;
	}
	/**
	 * In Seconds
	 * @return
	 */
	public void setMax_live(double max_live) {
		this.max_live = max_live;
	}
	
	
}
