package Kartoffel.Licht.Rendering;

import org.joml.Matrix4f;


/**
 * The camera class contains utilities for calculating a projectionMatrix, used by the Camera class.
 */
public class ProjectionBox {

	private float Z_NEAR = 0.05f;
	private float Z_FAR = 2500;
	private float FOV = (float) Math.toRadians(60);
	//0.5 = height, 2 = width
	private float aspectRatio = 1f;
	private float planeSize = 1;
	private Matrix4f projection = new Matrix4f();
	
	private float angleX = 0;
	private float angleY = 0;
	
	/**
	 *Creates a new perspective projection 
	 */
	public ProjectionBox() {
		update();
	}
	/**
	 * Creates a new orthogonal projection, viewing from -planeSize to planesize
	 * @param planeSize
	 */
	public ProjectionBox(float planeSize) {
		setPlaneSize(planeSize);
		updateOrtho();
	}
	/**
	 * Updates the projectionMatrix to be a perspective Matrix
	 */
	public void update() {
		projection.identity();
		projection.setPerspectiveOffCenter(FOV, angleX, angleY, aspectRatio, Z_NEAR, Z_FAR);
	}
	/**
	 * Updates the projectionMatrix to be an orthogonal Matrix
	 */
	public void updateOrtho() {
		float sx = planeSize*aspectRatio;
		float sy = planeSize/aspectRatio;
		projection.identity();
		projection.ortho(-sx, sx, -sy, sy, Z_NEAR, Z_FAR);
	}
	/**
	 * Returns the near plane
	 * @return
	 */
	public float getZ_NEAR() {
		return Z_NEAR;
	}
	/**
	 * Sets the near plane
	 * @param z_NEAR
	 */
	public void setZ_NEAR(float z_NEAR) {
		Z_NEAR = z_NEAR;
	}
	/**
	 * Returns the far plane
	 * @return
	 */
	public float getZ_FAR() {
		return Z_FAR;
	}
	/**
	 * Sets the far plane
	 * @param z_FAR
	 */
	public void setZ_FAR(float z_FAR) {
		Z_FAR = z_FAR;
	}
	/**
	 * Returns the FOV in degrees
	 * @return
	 */
	public float getFOV() {
		return (float) Math.toDegrees(FOV);
	}
	/**
	 * Sets the FOV
	 * @param FOV in degrees
	 */
	public void setFOV(float FOV) {
		FOV = (float) Math.toRadians(FOV);
	}
	/**
	 * Returns the projectionMatrix
	 * @return
	 */
	public Matrix4f getProjection() {
		return projection;
	}
	/**
	 * Sets the aspectRatio
	 * @param aspectRatio
	 */
	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	/**
	 * Sets the aspectRatio (double variant)
	 * @param aspectRatio
	 */
	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = (float) aspectRatio;
	}
	/**
	 * Returns the aspectRatio
	 * @return
	 */
	public float getAspectRatio() {
		return aspectRatio;
	}
	/**
	 * Returns the planeSize
	 * @return
	 */
	public float getPlaneSize() {
		return planeSize;
	}
	/**
	 * Sets the planeSize
	 * @param planeSize
	 */
	public void setPlaneSize(float planeSize) {
		this.planeSize = planeSize;
	}
	/**
	 * Sets the X angle
	 * @param angleX
	 */
	public void setAngleX(float angleX) {
		this.angleX = angleX;
	}
	/**
	 * Sets the Y angle
	 * @param angleY
	 */
	public void setAngleY(float angleY) {
		this.angleY = angleY;
	}
	/**
	 * Returns the X angle
	 * @return
	 */
	public float getAngleX() {
		return angleX;
	}
	/**
	 * Returns the Y Angle
	 * @return
	 */
	public float getAngleY() {
		return angleY;
	}
	/**
	 * Makes this projectionBox instance to a clone of the given one
	 * @param projectionBox
	 */
	public void set(ProjectionBox projectionBox) {
		this.aspectRatio = projectionBox.aspectRatio;
		this.FOV = projectionBox.FOV;
		this.projection.set(projectionBox.projection);
		this.Z_FAR = projectionBox.Z_FAR;
		this.Z_NEAR = projectionBox.Z_NEAR;
		this.planeSize = projectionBox.planeSize;
	}
	
	

}
