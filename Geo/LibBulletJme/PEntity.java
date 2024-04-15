package Kartoffel.Licht.Geo.LibBulletJme;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Quaternionf;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.RotationOrder;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.joints.New6Dof;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.infos.RigidBodyMotionState;
import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.util.BufferUtils;

import Kartoffel.Licht.Geo.AABB;
import Kartoffel.Licht.Rendering.GEntity;

/**
 * A PEntity is a kind of 'extension' for GEntities. Its , how the name suggest, an entity which manages a physical body.
 *
 */
public class PEntity {
	
	
	/** Sets this collision object as static. */
	public static final int STATIC_OBJECT            = 1;
	
	/** Sets this collision object as kinematic. */
	public static final int KINEMATIC_OBJECT         = 2;
	
	/** Disables contact response. */
	public static final int NO_CONTACT_RESPONSE      = 3;
	
	public static final int CHARACTER_OBJECT         = 4;
	
//	private static final float DEGREES_TO_RADIANS = 0.017453292519943295f;

	private GEntity gentity;
	
	private CollisionShape collShape;
	private PhysicsRigidBody body;
	private RigidBodyMotionState motionState;
	private Transform trans;
	
	private boolean isStatic = false;
	
	private int type;
	private Object[] args;
	private List<Object> keepAlive = new ArrayList<Object>();
	
	//Variables used for methods
	private BoundingBox b = new BoundingBox();
	private Vector3f center = new Vector3f();
	private Quaternion quad = new Quaternion();
	
	/**
	 * Types:<br>
	 * (Arguments as floats)<br>
	 * 0 - Static Plane(normalX, normalY, normalZ)<br>
	 * 1 - Sphere(radius)<br>
	 * 2 - Box(sizeX, sizeY, sizeZ)<br>
	 * 3 - Cylinder(halfExtendX, halfExtendY, halfExtendZ)<br>
	 * 4 - Capsule(radius, height)<br>
	 * 5 - Cone(radius, height)<br>
	 * 6 - #TODO<br>
	 * 7 - ConvexHullShape(List:Vector3f: points)<br>
	 * 8 - TriangleMeshShape(float[] vert, int[] indices)<br>
	 */
	public PEntity(GEntity gentity, float mass, int type, Object...shapeArg) {
		v(gentity, mass, type, shapeArg);
	}

	private void v(GEntity gentity, float mass, int type, Object...shapeArg) {
		this.type = type;
		this.args = new Object[3];
		System.arraycopy(shapeArg, 0, this.args, 0, shapeArg.length);
		this.gentity = gentity;
		this.gentity.setProperty("Physics", this);
		if(type == 0) {
			collShape = new PlaneCollisionShape(new Plane(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]), (float)shapeArg[3]));
		}
		else if(type == 1) {
			collShape = new SphereCollisionShape((float)shapeArg[0]);
		}
		else if(type == 2) {
			collShape = new BoxCollisionShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
		}
		else if(type == 3) {
			collShape = new CylinderCollisionShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
		}
		else if(type == 4) {
			collShape = new CapsuleCollisionShape((float)shapeArg[0], (float)shapeArg[1]);
		}
		else if(type == 5) {
			collShape = new ConeCollisionShape((float)shapeArg[0], (float)shapeArg[1]);
		}
		else if(type == 6) {
			throw new RuntimeException("Not implemented!");
		}
		else if(type == 8) {
			float[] vertices = (float[]) shapeArg[0];
			int[] indices = (int[]) shapeArg[1];
			ByteBuffer ver = BufferUtils.createByteBuffer(vertices.length*4);
			ver.asFloatBuffer().put(vertices);
			ByteBuffer ind = BufferUtils.createByteBuffer(indices.length*4);
			ind.asIntBuffer().put(indices);
			IndexedMesh im = new IndexedMesh(ver.asFloatBuffer(), ind.asIntBuffer());
			collShape = new MeshCollisionShape(true, im);
			keepAlive.add(ver);
			keepAlive.add(ind);
		}
		else {
			throw new RuntimeException("type not detected");
		}
		body = new PhysicsRigidBody(collShape);
		motionState = body.getMotionState();
		body.getTransform(trans);
		setMass(mass);
	}
	
	/**
	 * Sets the collider for this body.<br>
	 * Types:<br>
	 * (Arguments as floats)<br>
	 * 0 - Static Plane(normalX, normalY, normalZ)<br>
	 * 1 - Sphere(radius)<br>
	 * 2 - Box(sizeX, sizeY, sizeZ)<br>
	 * 3 - Cylinder(halfExtendX, halfExtendY, halfExtendZ)<br>
	 * 4 - Capsule(radius, height)<br>
	 * 5 - Cone(radius, height)<br>
	 * 6 - #TODO<br>
	 * 7 - ConvexHullShape(List:Vector3f: points)<br>
	 * 8 - TriangleMeshShape(float[] vert, int[] indices)<br>
	 * @param type
	 * @param shapeArg
	 */
	public void setCollision(int type, Object...shapeArg) {
		this.type = type;
		this.args = shapeArg;
		this.gentity.setProperty("Physics", this);
		if(type == 0) {
			collShape = new PlaneCollisionShape(new Plane(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]), (float)shapeArg[3]));
		}
		else if(type == 1) {
			collShape = new SphereCollisionShape((float)shapeArg[0]);
		}
		else if(type == 2) {
			collShape = new BoxCollisionShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
		}
		else if(type == 3) {
			collShape = new CylinderCollisionShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
		}
		else if(type == 4) {
			collShape = new CapsuleCollisionShape((float)shapeArg[0], (float)shapeArg[1]);
		}
		else if(type == 5) {
			collShape = new ConeCollisionShape((float)shapeArg[0], (float)shapeArg[1]);
		}
		else if(type == 6) {
			throw new RuntimeException("Not implemented!");
		}
		else if(type == 8) {
			float[] vertices = (float[]) shapeArg[0];
			int[] indices = (int[]) shapeArg[1];
			ByteBuffer ver = BufferUtils.createByteBuffer(vertices.length*4);
			ver.asFloatBuffer().put(vertices);
			ByteBuffer ind = BufferUtils.createByteBuffer(indices.length*4);
			ind.asIntBuffer().put(indices);
			IndexedMesh im = new IndexedMesh(ver.asFloatBuffer(), ind.asIntBuffer());
			collShape = new MeshCollisionShape(true, im);
			keepAlive.clear();
			keepAlive.add(ver);
			keepAlive.add(ind);
		}
		else {
			throw new RuntimeException("type not detected");
		}
		body.setCollisionShape(collShape);
	}
	
	/**
	 *Synchronizes the P- and the GEntity
	 */
	void update() {
		if(!isStatic) {
			this.gentity.setUpdate(false); //Matrix is set directly by JBullet
			Matrix4f f = new Matrix4f();
			body.getTransform(trans).toTransformMatrix(f);
			gentity.getTransformationMatrix().m00(f.m00);
			gentity.getTransformationMatrix().m01(f.m10);
			gentity.getTransformationMatrix().m02(f.m20);
			gentity.getTransformationMatrix().m03(f.m30);
			gentity.getTransformationMatrix().m10(f.m01);
			gentity.getTransformationMatrix().m11(f.m11);
			gentity.getTransformationMatrix().m12(f.m21);
			gentity.getTransformationMatrix().m13(f.m31);
			gentity.getTransformationMatrix().m20(f.m02);
			gentity.getTransformationMatrix().m21(f.m12);
			gentity.getTransformationMatrix().m22(f.m22);
			gentity.getTransformationMatrix().m23(f.m32);
			gentity.getTransformationMatrix().m30(f.m03);
			gentity.getTransformationMatrix().m31(f.m13);
			gentity.getTransformationMatrix().m32(f.m23);
			gentity.getTransformationMatrix().m33(f.m33);
			gentity.getTransformationMatrix().scale(gentity.getScale());
			gentity.getPosition().set(getPosition());
			gentity.getRotation().set(getRotation());
		}
		else {
			setPosition(gentity.getPosition().x, gentity.getPosition().y, gentity.getPosition().z);
			setRotation(gentity.getRotation().x, gentity.getRotation().y, gentity.getRotation().z);
		}
	}
	//#################Constraints
	public PEntity link(PEntity entity, org.joml.Vector3f relP, float stiff) {
		New6Dof c = new New6Dof(body, entity.body, new Vector3f(0, 0, 0), new Vector3f(relP.x, relP.y, relP.z), Matrix3f.IDENTITY, Matrix3f.IDENTITY, RotationOrder.XYZ);
		c.setStiffness(0, stiff, true);
		c.setStiffness(1, stiff, true);
		c.setStiffness(2, stiff, true);
		c.setStiffness(3, stiff, true);
		c.setStiffness(4, stiff, true);
		c.setStiffness(5, stiff, true);
		Physics.pspace.add(c);
		return this;
	}
	public PEntity link(PEntity entity, float x, float y, float z, float stiff) {
		New6Dof c = new New6Dof(body, entity.body, new Vector3f(0, 0, 0), new Vector3f(x, y, z), Matrix3f.IDENTITY, Matrix3f.IDENTITY, RotationOrder.XYZ);
		Physics.pspace.add(c);
		c.setStiffness(0, stiff, true);
		c.setStiffness(1, stiff, true);
		c.setStiffness(2, stiff, true);
		c.setStiffness(3, stiff, true);
		c.setStiffness(4, stiff, true);
		c.setStiffness(5, stiff, true);
		return this;
	}
	public PEntity link(PEntity entity, org.joml.Vector3f pivotA, org.joml.Vector3f pivotB, org.joml.Matrix3f rotationA, org.joml.Matrix3f rotationB, float stiff) {
		New6Dof c = new New6Dof(body, entity.body, new Vector3f(pivotA.x, pivotA.y, pivotA.z), new Vector3f(pivotB.x, pivotB.y, pivotB.z),
				new Matrix3f(
						rotationA.m00, rotationA.m10, rotationA.m20,
						rotationA.m01, rotationA.m11, rotationA.m21,
						rotationA.m02, rotationA.m12, rotationA.m22
						),
				new Matrix3f(
						rotationB.m00, rotationB.m10, rotationB.m20,
						rotationB.m01, rotationB.m11, rotationB.m21,
						rotationB.m02, rotationB.m12, rotationB.m22
						), RotationOrder.XYZ);
		c.setStiffness(0, stiff, true);
		c.setStiffness(1, stiff, true);
		c.setStiffness(2, stiff, true);
		c.setStiffness(3, stiff, true);
		c.setStiffness(4, stiff, true);
		c.setStiffness(5, stiff, true);
		Physics.pspace.add(c);
		return this;
	}
	public PEntity pin(org.joml.Vector3f position, float stiff) {
		New6Dof c = new New6Dof(body, new Vector3f(0, 0, 0), new Vector3f(position.x, position.y, position.z), Matrix3f.IDENTITY, Matrix3f.IDENTITY, RotationOrder.XYZ);
		Physics.pspace.add(c);
		c.setStiffness(0, stiff, true);
		c.setStiffness(1, stiff, true);
		c.setStiffness(2, stiff, true);
		c.setStiffness(3, stiff, true);
		c.setStiffness(4, stiff, true);
		c.setStiffness(5, stiff, true);
		return this;
	}
	public PEntity pin(float x, float y, float z, float stiff) {
		New6Dof c = new New6Dof(body, new Vector3f(0, 0, 0), new Vector3f(x, y, z), Matrix3f.IDENTITY, Matrix3f.IDENTITY, RotationOrder.XYZ);
		c.setStiffness(0, stiff, true);
		c.setStiffness(1, stiff, true);
		c.setStiffness(2, stiff, true);
		c.setStiffness(3, stiff, true);
		c.setStiffness(4, stiff, true);
		c.setStiffness(5, stiff, true);
		Physics.pspace.add(c);
		return this;
	}
	
	
	//####################################
	/**
	 * Returns the GEntity
	 * @return
	 */
	public GEntity getGentity() {
		return gentity;
	}
	/**
	 * Sets the GEntity.
	 * @param gentity
	 * @return
	 */
	public PEntity setGentity(GEntity gentity) {
		this.gentity = gentity;
		return this;
	}
	/**
	 * Returns the Bounding box of the collider
	 * @return
	 */
	public AABB getAABB() {
		body.boundingBox(b);
		b.getCenter(center);
		return new AABB(center.x-b.getXExtent(), center.y-b.getYExtent(), center.z-b.getZExtent(), center.x+b.getXExtent(), center.y+b.getYExtent(), center.z+b.getZExtent());
	}
	/**
	 * Sets the position of the PEntity
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity setPosition(float x, float y, float z) {
		body.activate();
		center.x = x;
		center.y = y;
		center.z = z;
		body.setPhysicsLocation(center);
		return this;
	}
	/**
	 * Returns the position of the PEntity
	 * @return
	 */
	public org.joml.Vector3f getPosition() {
		body.getPhysicsLocation(center);
		return new org.joml.Vector3f(center.x, center.y, center.z);
	}
	/**
	 * Restrains an axis to the given value.<br>
	 * axis:<br>
	 *   0 == x<br>
	 *   1 == y<br>
	 *   2 == z<br>
	 * @param axis
	 * @param value
	 * @return
	 */
	public PEntity restrainPosition(int axis, float value) {
		body.activate();
		body.getPhysicsLocation(center);
		if(axis == 0)
			center.x = value;
		else if(axis == 1)
			center.y = value;
		else
			center.z = value;
		body.setPhysicsLocation(center);
		return this;
	}
	/**
	 * Returns the rotation
	 * @return rotation in degrees
	 */
	public org.joml.Vector3f getRotation() {
		body.getPhysicsRotation(quad);
		Quaternionf q = new Quaternionf(quad.getX(), quad.getY(), quad.getZ(), quad.getW());
		return q.getEulerAnglesXYZ(new org.joml.Vector3f()).mul(57.29577951308232f);
	}
	/**
	 * Sets the rotation
	 * @param x in degrees
	 * @param y in degrees
	 * @param z in degrees
	 * @return
	 */
	public PEntity setRotation(float x, float y, float z) {
		body.activate();
		quad.fromAngles(x, y, z);
		body.setPhysicsRotation(quad);
		return this;
	}
	/**
	 * Sets the rotation
	 * @param x in quaternion
	 * @param y in quaternion
	 * @param z in quaternion
	 * @param w in quaternion
	 * @return
	 */
	public PEntity setRotation(float x, float y, float z, float w) {
		body.activate();
		quad.set(x, y, z, w);
		body.setPhysicsRotation(quad);
		return this;
	}
	/**
	 * Restrains an axis to the given value.<br>
	 * axis:<br>
	 *   0 == x<br>
	 *   1 == y<br>
	 *   2 == z<br>
	 * @param axis
	 * @param value in degrees
	 * @return
	 */
	public PEntity restrainRotation(int axis, float value) {
		body.activate();
		org.joml.Vector3f rot = getRotation();
		if(axis == 0)
			rot.x = (float)Math.toRadians(axis);
		else if(axis == 1)
			rot.y = (float)Math.toRadians(axis);
		else
			rot.z = (float)Math.toRadians(axis);
		setRotation(rot.x, rot.y, rot.z);
		return this;
	}
	
	/**
	 * Applies a central force onto the body
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity applyCentralForce(float x, float y, float z) {
		body.activate();
		body.applyCentralForce(new Vector3f(x, y, z));
		return this;
	}
	/**
	 * Applies a central Impulse
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity applyCentralImpulse(float x, float y, float z) {
		body.activate();
		body.applyCentralImpulse(new Vector3f(x, y, z));
		return this;
	}
	/**
	 * Applies a force relative to the center
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @return
	 */
	public PEntity applyForce(float x, float y, float z, float rx, float ry, float rz) {
		body.activate();
		body.applyForce(new Vector3f(x, y, z), new Vector3f(rx, ry, rz));
		return this;
	}
	/**
	 * Applies an impulse relative to the center
	 * @param x
	 * @param y
	 * @param z
	 * @param rx
	 * @param ry
	 * @param rz
	 * @return
	 */
	public PEntity applyImpulse(float x, float y, float z, float rx, float ry, float rz) {
		body.activate();
		body.applyImpulse(new Vector3f(x, y, z), new Vector3f(rx, ry, rz));
		return this;
	}
	
	/**
	 * Applies torque.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity applyTorque(float x, float y, float z) {
		body.activate();
		body.applyTorque(new Vector3f(x, y, z));
		return this;
	}
	/**
	 * Applies a torque impulse
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity applyTorqueImpulse(float x, float y, float z) {
		body.activate();
		body.applyTorqueImpulse(new Vector3f(x, y, z));
		return this;
	}
	
	/**
	 * Sets the motion
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity setPositionMotion(float x, float y, float z) {
		body.activate();
		body.setLinearVelocity(new Vector3f(x, y, z));
		return this;
	}
	/**
	 * Sets the rotational motion
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public PEntity setRotationMotion(float x, float y, float z) {
		body.activate();
		body.setAngularVelocity(new Vector3f(x, y, z));
		return this;
	}
	/**
	 * Returns the motion
	 * @return
	 */
	public org.joml.Vector3f getPositionMotion() {
		body.activate();
		Vector3f v = new Vector3f();
		body.getLinearVelocity(v);
		return new org.joml.Vector3f(v.x, v.y, v.z);
	}
	/**
	 * Returns the rotational motion
	 * @return
	 */
	public org.joml.Vector3f getRotationMotion() {
		body.activate();
		Vector3f v = new Vector3f();
		body.getAngularVelocity(v);
		return new org.joml.Vector3f(v.x, v.y, v.z);
	}
	/**
	 * Activates the body
	 */
	public void activate() {
		body.activate();
	}
	/**
	 * Sets this body to be a character
	 */
	public void setCharacter() {
		body.setSleepingThresholds(0, 0);
		body.setAngularFactor(0);
	}
	/**
	 * Sets the mass of the body
	 * @param mass
	 * @return
	 */
	public PEntity setMass(float mass) {
		body.setMass(mass);
		return this;
	}
	/**
	 * Returns the mass of the body
	 * @return
	 */
	public float getMass() {
		return body.getMass();
	}
	/**
	 * Sets this body to be kinematic
	 * @return
	 */
	public PEntity setKinematic(boolean isKinematic) {
		body.setKinematic(isKinematic);
		return this;
	}
	/**
	 * Returns the activation state
	 * @return
	 */
	public int getActivationState() {
		return body.getActivationState();
	}
	
	/**
	 * Returns the rigidbody
	 * @return
	 */
	public PEntity setGravity(float dx, float dy, float dz) {
		body.setGravity(new Vector3f(dx, dy, dz));
		return this;
	}
	
	/**
	 * Returns the type of the body
	 * @return
	 */
	public int getType() {
		return type;
	}
	/**
	 * Returns the Arguments used to generate the body
	 * @return
	 */
	public Object[] getArgs() {
		return args;
	}
	/**
	 * Sets the friction
	 * @param friction
	 * @return
	 */
	public PEntity setFriction(float friction) {
		body.setFriction(friction);
		return this;
	}
	/**
	 * Returns the friction
	 * @return
	 */
	public float getFriction() {
		return body.getFriction();
	}
	/**
	 * Returns the collision shape
	 * @return
	 */
	public CollisionShape getCollShape() {
		return collShape;
	}
	/**
	 * Sets the collision shape
	 * @param collShape
	 * @return
	 */
	public PEntity setCollShape(CollisionShape collShape) {
		this.collShape = collShape;
		return this;
	}
	/**
	 * Returns the rigidbody
	 * @return
	 */
	public PhysicsRigidBody getBody() {
		return body;
	}

	/**
	 * Returns the current motion state
	 * @return
	 */
	public RigidBodyMotionState getMotionState() {
		return motionState;
	}
	/**
	 * Returns the bodys transform
	 * @return
	 */
	public Transform getTrans() {
		return trans;
	}
	/**
	 * Returns if the body is static. Static PEntities will, instead of passing their own transform to the GEntity, take the transform of the GEntity.
	 * @return
	 */
	public boolean isStatic() {
		return isStatic;
	}
	/**
	 * Sets the body to be static. Static PEntities will, instead of passing their own transform to the GEntity, take the transform of the GEntity.
	 * @param isStatic
	 * @return
	 */
	public PEntity setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
}
