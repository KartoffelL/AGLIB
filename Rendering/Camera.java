package Kartoffel.Licht.Rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import Kartoffel.Licht.Java.namable;

/**
 * The camera class contains utilities for calculating Matrices related to camera movement/projection
 *
 */
public class Camera implements namable{
	
	/**Global Up Vector, normally [0, 1, 0]*/
	public static final Vector3f Up = new Vector3f(0, 1, 0);
	
	protected Vector3f position;
	protected Vector3f rotation;
	protected ProjectionBox projectionBox;
	
	protected Matrix4f viewMatrix;
	protected Matrix4f viewMatrixInverse;
	protected Vector3f[] rotvec;
	
	protected Matrix4f ProViewMatrix;
	
	protected float distance = 0;
	
	protected boolean isOrtho = false;
	
	public static final boolean doPositionOffset = false;
	
	private String name = "Camera";
	
	/**
	 * Creates a new perspective camera with the default ProjectionBox
	 */
	public Camera() {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		projectionBox = new ProjectionBox();
		 viewMatrix = new Matrix4f();
		 viewMatrixInverse = new Matrix4f();
		 ProViewMatrix = new Matrix4f();
		 rotvec = new Vector3f[3];
		 rotvec[0] = new Vector3f(1, 0, 0);
		 rotvec[1] = new Vector3f(0, 1, 0);
		 rotvec[2] = new Vector3f(0, 0, 1);
	}
	/**
	 * Creates a new orthogonal camera viewing from -planeSize to planesize
	 * @param PlaneSize
	 */
	public Camera(float PlaneSize) {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		projectionBox = new ProjectionBox(PlaneSize);
		 viewMatrix = new Matrix4f();
		 viewMatrixInverse = new Matrix4f();
		 ProViewMatrix = new Matrix4f();
		 rotvec = new Vector3f[3];
		 rotvec[0] = new Vector3f(1, 0, 0);
		 rotvec[1] = new Vector3f(0, 1, 0);
		 rotvec[2] = new Vector3f(0, 0, 1);
		 this.setOrtho(true);
	}
	
	/**
	 * Creates a new copy of the given camera
	 * @param cam
	 */
	public Camera(Camera cam) {
		position = new Vector3f(0, 0, 1);
		rotation = new Vector3f(0, 0, 0);
		projectionBox = new ProjectionBox();
		 viewMatrix = new Matrix4f();
		 viewMatrixInverse = new Matrix4f();
		 ProViewMatrix = new Matrix4f();
		 rotvec = new Vector3f[3];
		 rotvec[0] = new Vector3f(1, 0, 0);
		 rotvec[1] = new Vector3f(0, 1, 0);
		 rotvec[2] = new Vector3f(0, 0, 1);
		this.set(cam);
	}
	
	/**
	 * Sets the position Vector of the camera. Any updates to the given vector will be reflected to the cameras position.<br>
	 * Use 'getPosition().set(position)' to only copy the position
	 * @param position
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	/**
	 * Adds the offset to the position of the camera
	 * @param position
	 */
	public void addPosition(Vector3f offset) {
		this.position.add(offset);
	}
	
	/**
	 * Returns the position vector
	 * @return
	 */
	public Vector3f getPosition() {
		return position;
	}
	/**
	 * Returns the position moved back by the eye-vector times distance
	 * @return
	 */
	public Vector3f getPositionDistanced() {
		return new Vector3f(viewMatrix.get(0, 2), viewMatrix.get(1, 2), viewMatrix.get(2, 2)).mul(distance).sub(position).negate();
	}
	
	/**
	 * Sets the rotation Vector of the camera (degrees). Any updates to the given vector will be reflected to the cameras rotation.<br>
	 * Use 'getRotation().set(rotation)' to only copy the rotation
	 * @param rotation in degrees
	 */
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	/**
	 * Adds the offset to the rotation of the camera
	 * @param offset in degrees
	 */
	public void addRotation(Vector3f offset) {
		this.rotation.add(offset);
	}
	/**
	 * Returns the rotation vector (degrees) of the camera
	 * @return
	 */
	public Vector3f getRotation() {
		return rotation;
	}
	/**
	 * Returns the Projection Matrix of the camera. Effectively equivalent to 'getProjectionBox().getProjection()'
	 * @return
	 */
	public Matrix4f getProjection() {
		return projectionBox.getProjection();
	}
	/**
	 * Returns the projectionBox of the camera.
	 * @return
	 */
	public ProjectionBox getProjectionBox() {
		return projectionBox;
	}
	
	/**
	 * Updates the viewMatrix and calculates the inverse of it. Also updates the projectionBox, after thus the proViewMatrix is calculated.<br>
	 *  Use 'updateWithViewMatrix' to avoid recalculating the viewMatrix if its already calculated externally
	 */
	public void update() {
		//Position offset
		if(doPositionOffset) {
			GEntity.positionOffset.x = (int) -position.x; //TODO
			GEntity.positionOffset.y = (int) -position.y;
			GEntity.positionOffset.z = (int) -position.z;
		}
		
		if(!isOrtho)
			projectionBox.update();
		else
			projectionBox.updateOrtho();
		
		getViewMatrixNew(viewMatrix);
		viewMatrix.get(viewMatrixInverse);
		viewMatrixInverse.invert();
		projectionBox.getProjection().mul(viewMatrix, ProViewMatrix);
	}
	
	/**
	 * Calculates the inverse of the viewMatrix, updates the projectionBox and calculates the ProViewMatrix.<br>
	 * Use if the viewMatrix is calculated externally
	 */
	public void update(boolean updateViewMatrix, boolean updateProjectionMatrix) {
		//Position offset
		if(doPositionOffset) {
			GEntity.positionOffset.x = (int) -position.x; //TODO
			GEntity.positionOffset.y = (int) -position.y;
			GEntity.positionOffset.z = (int) -position.z;
		}
		if(updateViewMatrix)
			getViewMatrixNew(viewMatrix);
		if(updateProjectionMatrix)
			if(!isOrtho)
				projectionBox.update();
			else
				projectionBox.updateOrtho();
		viewMatrix.get(viewMatrixInverse);
		viewMatrixInverse.invert();
		projectionBox.getProjection().mul(viewMatrix, ProViewMatrix);
	}
	
	/**Use 'setDistance(float distance)' instead*/
	@Deprecated
	public void updateP(float distance) {
		getViewMatrixNew(viewMatrix);
		double hd = Math.cos(Math.toRadians(rotation.x));
		double vd = Math.sin(Math.toRadians(rotation.x));
		
		
		viewMatrix.translate(
				(float)(hd*Math.cos(Math.toRadians(rotation.y-90)))*distance,
				-(float)(vd)*distance,
				(float)(hd*Math.sin(Math.toRadians(rotation.y-90)))*distance
					);
		
		viewMatrix.get(viewMatrixInverse);
		viewMatrixInverse.invert();

		projectionBox.getProjection().mul(viewMatrix, ProViewMatrix);
	}
	/**
	 * Returns the viewMatrix
	 * @return
	 */
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	/**
	 * Returns the inverse of the viewMatrix
	 * @return
	 */
	public Matrix4f getViewMatrixInv() {
		return viewMatrixInverse;
	}
	/**
	 * Calculates the new viewMatrix using position, rotation, etc... contained in the camera
	 * @param dest
	 * @return
	 */
	public Matrix4f getViewMatrixNew(Matrix4f dest) {
		dest.identity();
		dest.rotate((float) Math.toRadians(rotation.x), rotvec[0])
		.rotate((float) Math.toRadians(rotation.y), rotvec[1])
		.rotate((float) Math.toRadians(rotation.z), rotvec[2]);
		if(doPositionOffset)
			dest.translate(-position.x % 1, -position.y % 1, -position.z % 1);
		else
			dest.translate(-position.x, -position.y, -position.z);
		if(distance != 0)
			dest.translate(viewMatrix.get(0, 2)*distance, viewMatrix.get(1, 2)*distance, viewMatrix.get(2, 2)*distance);
		return dest;
	}
	/**
	 * Returns the eye vector of the viewMatrix
	 * @return
	 */
	public Vector3f getOutDirection() {
		return new Vector3f(viewMatrix.get(0, 2), viewMatrix.get(1, 2), viewMatrix.get(2, 2));
	}
	/**
	 * Returns the up vector of the viewMatrix
	 * @return
	 */
	public Vector3f getOutUp() {
		return new Vector3f(viewMatrix.get(0, 1), viewMatrix.get(1, 1), viewMatrix.get(2, 1));
	}
	/**
	 * Returns the proViewMatrix (The product of the projection- and viewMatrix (P * V))
	 * @return
	 */
	public Matrix4f getProViewMatrix() {
		return ProViewMatrix;
	}
	/**
	 * If this Camera has an orthogonal projection
	 * @return
	 */
	public boolean isOrtho() {
		return isOrtho;
	}
	/**
	 * Sets whenever this Camera should have an orthogonal projection
	 * @param isOrtho
	 */
	public void setOrtho(boolean isOrtho) {
		this.isOrtho = isOrtho;
	}
	/**
	 * Sets the distance the viewMatrix is, along the eye vector, offset by
	 * @param distance
	 */
	public void setDistance(float distance) {
		this.distance = distance;
	}
	/**
	 * Returns the distance the viewMatrix is, along the eye vector, offset by
	 * @return
	 */
	public float getDistance() {
		return distance;
	}
	/**
	 * Makes this camera instance to a clone of the given one
	 * @param cam
	 */
	public void set(Camera cam) {
		this.isOrtho = cam.isOrtho;
		this.position.set(cam.position);
		this.rotation.set(cam.rotation);
		this.projectionBox.set(cam.projectionBox);
		this.ProViewMatrix.set(cam.ProViewMatrix);
		this.viewMatrix.set(cam.viewMatrix);
		this.viewMatrixInverse.set(cam.viewMatrixInverse);
		this.distance = cam.distance;
		this.rotvec[0] = cam.rotvec[0];
		this.rotvec[1] = cam.rotvec[1];
		this.rotvec[2] = cam.rotvec[2];
	}
	
	@Override
	public String toString() {
		return "[" + position.toString() + " " + rotation.toString() + " " + isOrtho + "]";
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
