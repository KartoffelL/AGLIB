package Kartoffel.Licht.Rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import Kartoffel.Licht.Geo.DefinableShape;
import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Animation.Animation;
import Kartoffel.Licht.Rendering.Animation.AnimationManager;
import Kartoffel.Licht.Rendering.Animation.StaticAnimation;
import Kartoffel.Licht.Rendering.Shapes.ShapeData;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.TextureMaterial;

/**
 * A GEntity is a graphical Entity. It contains a Model to Render, A texture and positions.<br>
 * 
 * 
 * 
 */

public class GEntity implements freeable, namable{
	
	protected String name = "GEntity";
	protected int ID;
	protected Model mod;
	protected Renderable tex;
	protected Matrix4f transformation;
	protected Matrix4f transformationInv;
	protected volatile Vector3f position;
	public static Vector3i positionOffset = new Vector3i();
	
	protected volatile Vector3f rotation; //IN degrees
	protected volatile Vector3f scale;
	protected volatile boolean render = true;
	protected boolean update = true;
	
	protected Animation animation = null;
	
	protected DefinableShape shape = null;
	
	protected Map<String, Object> properties;

	/**
	 * Creates a new GEntity using the Texture and the ShapeData
	 * 
	 */
	public GEntity(Renderable texture, ShapeData shape) {
		this(texture, new Model(shape));
	}
	/**
	 * Creates a new GEntity using the Texture and the Model
	 * 
	 */
	public GEntity(Renderable tex, Model mod) {
		this.mod = mod;
		this.transformation = new Matrix4f();
		this.transformationInv = new Matrix4f();
		this.scale = new Vector3f(1, 1, 1);
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);
		this.tex = tex;
		this.ID = new Random().nextInt();
		this.properties = new HashMap<String, Object>();
	}
	/**
	 * Creates a new named GEntity using the Texture and the Model
	 * 
	 */
	public GEntity(Renderable tex, Model mod, String name) {
		this.mod = mod;
		this.transformation = new Matrix4f();
		this.transformationInv = new Matrix4f();
		this.scale = new Vector3f(1, 1, 1);
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);
		this.tex = tex;
		this.ID = new Random().nextInt();
		this.name = name;
		this.properties = new HashMap<String, Object>();
	}
	/**
	 * Creates a new GEntity using default Texture/Model
	 */
	public GEntity() {
		this.mod = new Model();
		this.transformation = new Matrix4f();
		this.transformationInv = new Matrix4f();
		this.scale = new Vector3f(0.01f);
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);
		this.tex = new TextureMaterial();
		this.ID = new Random().nextInt();
		this.properties = new HashMap<String, Object>();
	}
	/**
	 * Creates a new GEntity to be a clone of the given one. Does not copy the Texture nor the Model
	 * @param gentity
	 */
	public GEntity(GEntity gentity) {
		this.mod = gentity.mod;
		this.transformation = new Matrix4f(gentity.transformation);
		this.transformationInv = new Matrix4f(gentity.transformationInv);
		this.scale = new Vector3f(gentity.scale);
		this.position = new Vector3f(gentity.position);
		this.rotation = new Vector3f(gentity.rotation);
		this.tex = gentity.tex;
		this.ID = new Random().nextInt();
		this.properties = new HashMap<String, Object>(gentity.properties);
	}
	/**
	 * Modify individual instances of the Model<br>
	 * May throw a RuntimeException if the Model is not instanceable.
	 */
	public GEntity setInstance(int num, float[] data) {
		if(this.mod instanceof InstancedModel) {
			InstancedModel imod = (InstancedModel)this.mod;
			imod.setInstanceData(num, data);
			
		}
		else
			throw new RuntimeException("Model isin´t instanceable!");
		return this;
	}

	/**
	 * Returns the Model of this GEntity
	 * @return
	 */
	public Model getMod() {
		return mod;
	}
	/**
	 * Sets the Model of this GEntity
	 * @param mod
	 * @return
	 */
	public GEntity setMod(Model mod) {
		this.mod = mod;
		return this;
	}
	/**
	 * Returns the (if 'update' is set to true, updated) transformationMatrix
	 * @return
	 */
	public Matrix4f getTransformationMatrix() {
		if(!update)
			return transformation;
		updateTransformationMatrix();
		return transformation;
	}
	/**
	 * Returns the current transformationMatrix
	 * @return
	 */
	public Matrix4f getTransformationMatrixRaw() {
		return transformation;
	}
	/**
	 * Updates the transformationMatrix
	 * @return
	 */
	public GEntity updateTransformationMatrix() {
		transformation.identity();
		transformation.translate(position);
		if(positionOffset != null)
			transformation.translate(positionOffset.x, positionOffset.y, positionOffset.z);
		transformation.rotateX((float) Math.toRadians(rotation.x))
		.rotateY((float) Math.toRadians(rotation.y))
		.rotateZ((float) Math.toRadians(rotation.z))
		.scale(scale);
		return this;
	}
	/**
	 * Generates a new transformationMatrix
	 * @param position
	 * @param rotation
	 * @param scale
	 * @param dest
	 * @return
	 */
	public static Matrix4f create(Vector3f position, Vector3f rotation, Vector3f scale, Matrix4f dest) {
		dest.identity();
		if(position != null)
			dest.translate(position);
		if(positionOffset != null)
			dest.translate(positionOffset.x, positionOffset.y, positionOffset.z);
		if(rotation != null)
			dest.rotateX((float) Math.toRadians(rotation.x))
			.rotateY((float) Math.toRadians(rotation.y))
			.rotateZ((float) Math.toRadians(rotation.z));
		if(scale != null)
			dest.scale(scale);
		return dest;
	}
	/**
	 * Returns the inverse of  the transformationMatrix
	 * @return
	 */
	public Matrix4f getTransformationMatrixInv() {
		transformation.get(transformationInv);
		transformationInv.invert();
		return transformationInv;
	}
	/**
	 * Sets the transformationMatrix to 'matrix'. All updates to 'matrix' will be reflected back to this GEntity.<br>
	 * Use 'getTransformationMatrixRaw().set(matrix)' to only copy the Matrix
	 * @param matrix
	 * @return
	 */
	public GEntity setTransformation(Matrix4f matrix) {
		this.transformation = matrix;
		return this;
	}
	/**
	 * Returns the Position
	 * @return
	 */
	public Vector3f getPosition() {
		return position;
	}
	/**
	 * Sets the position vector to 'position'. All updates to 'position' will be reflected back to this GEntity.<br>
	 * Use 'getPosition().set(position)' to only copy the position
	 * @param position
	 * @return
	 */
	public GEntity setPosition(Vector3f position) {
		this.position = position;
		return this;
	}
	/**
	 * Returns the rotation
	 * @return rotation in degrees
	 */
	public Vector3f getRotation() {
		return rotation;
	}
	/**
	 * Sets the rotation (degrees) vector to 'rotation'. All updates to 'rotation' will be reflected back to this GEntity.<br>
	 * Use 'getRotation().set(rotation)' to only copy the rotation
	 * @param rotation in degrees
	 * @return
	 */
	public GEntity setRotation(Vector3f rotation) {
		this.rotation = rotation;
		return this;
	}
	/**
	 * Sets the "true" rotation. May Work if trying to rotate this GEntity with the camera. Only the Z and Y are affected.
	 * @param rotation in degrees
	 * @return
	 */
	public GEntity setTrueRotation(Vector3f rotation) {
		this.rotation.z = rotation.x;
		this.rotation.y = -rotation.y-90;
		return this;
	}
	/**
	 * Returns the scale
	 * @return
	 */
	public Vector3f getScale() {
		return scale;
	}
	/**
	 * Sets the scale vector to 'scale'. All updates to 'scale' will be reflected back to this GEntity.<br>
	 * Use 'getScale().set(scale)' to only copy the scale
	 * @param scale
	 * @return
	 */
	public GEntity setScale(Vector3f scale) {
		this.scale = scale;
		return this;
	}
	/**
	 * Returns if this GEntity should be drawn
	 * @return
	 */
	public boolean isRender() {
		return render;
	}
	/**
	 * Sets whenever this GEntity should be drawn
	 * @param render
	 * @return
	 */
	public GEntity setRender(boolean render) {
		this.render = render;
		return this;
	}
	/**
	 * Adds 'offset' to the position
	 * @param offset
	 * @return
	 */
	public GEntity addPosition(Vector3f offset) {
		this.position.add(offset);
		return this;
	}
	/**
	 * Adds 'offset' to rotation
	 * @param offset in degrees
	 * @return
	 */
	public GEntity addRotation(Vector3f offset) {
		this.rotation.add(offset);
		return this;
	}
	/**
	 * Sets the current animation
	 * @param anim
	 * @return
	 */
	public GEntity setAnimation(Animation anim) {
		this.animation = anim;
		return this;
	}
	/**
	 * Returns the current animation
	 * @return
	 */
	public Animation getAnimation() {
		return animation;
	}
	/**
	 * Returns the current animationManager, or null if current animation is not an animationManager
	 * @return
	 */
	public AnimationManager getAnimationManager() {
		if(animation instanceof AnimationManager)
			return (AnimationManager) animation;
		return null;
	}
	/**
	 * Returns the current static animation, or null if current animation is not an static animation
	 * @return
	 */
	public StaticAnimation getStaticAnimation() {
		if(animation instanceof StaticAnimation)
			return (StaticAnimation) animation;
		return null;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the Texture
	 * @return
	 */
	public Renderable getTex() {
		return tex;
	}
	/**
	 * Sets the Texture
	 * @param tex
	 * @return
	 */
	public GEntity setTex(Renderable tex) {
		this.tex = tex;
		return this;
	}
	/**
	 * Frees the Model and Texture assigned to this GEntity. Should not be used if one of them is shared
	 */
	public void free() {
		if(this.tex != null)
			this.tex.free();
		if(this.mod != null)
			this.mod.free();
	}
	/**
	 * Returns the ID of this GEntity
	 * @return
	 */
	public int getID() {
		return ID;
	}
	/**
	 * Sets whenever the transformationMatrix should be updated. Should be set to false if GEntity is controlled by e.g. a PEntity
	 * @param update
	 * @return
	 */
	public GEntity setUpdate(boolean update) {
		this.update = update;
		return this;
	}
	/**
	 * Sets the Shape that this GEntity defines. Generally should be a rough hitbox of the shape
	 * @param shape
	 * @return
	 */
	public GEntity setShape(DefinableShape shape) {
		this.shape = shape;
		return this;
	}
	/**
	 * Returns the Shape of this GEntity
	 * @return
	 */
	public DefinableShape getShape() {
		return shape;
	}
	@Override
	public String toString() {
		return "[ID:"+ID+"][Pos:"+position.toString()+"][Rot:"+rotation.toString()+"][Sca:"+scale.toString()+"]";
	}
	/**
	 * Sets a property of this GEntity
	 * @param var
	 * @param val
	 * @return
	 */
	public GEntity setProperty(String var, Object val) {
		properties.put(var, val);
		return this;
	}
	/**
	 * Returns a property, or null if none was defined
	 * @param var
	 * @return
	 */
	public Object getProperty(String var) {
		return properties.get(var);
	}
	/**
	 * Creates a new GEntity, effectively a clone of this one.<br>
	 * Texture and Model are shared and not copied
	 */
	public GEntity clone() {
		return new GEntity(this);
	}
}
