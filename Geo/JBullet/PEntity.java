package Kartoffel.Licht.Geo.JBullet;

/**
 * A PEntity is a kind of 'extension' for GEntities. Its , how the name suggest, an entity which manages a physical body.
 *
 */
//Using cz.advel.jbullet
@Deprecated
public class PEntity {
//	
//	
//	/** Sets this collision object as static. */
//	public static final int STATIC_OBJECT            = 1;
//	
//	/** Sets this collision object as kinematic. */
//	public static final int KINEMATIC_OBJECT         = 2;
//	
//	/** Disables contact response. */
//	public static final int NO_CONTACT_RESPONSE      = 3;
//	
//	public static final int CHARACTER_OBJECT         = 4;
//	
////	private static final float DEGREES_TO_RADIANS = 0.017453292519943295f;
//
//	private GEntity gentity;
//	
//	private CollisionShape collShape;
//	private RigidBodyConstructionInfo info;
//	private RigidBody body;
//	private MotionState motionState;
//	private Transform trans;
//	
//	private boolean isStatic = false;
//	
//	private int type;
//	private Object[] args;
//	private List<Object> keepAlive = new ArrayList<Object>();
//	
//	private static org.joml.Matrix4f mat = new org.joml.Matrix4f();
//	
//	/**
//	 * Types:<br>
//	 * (Arguments as floats)<br>
//	 * 0 - Static Plane(normalX, normalY, normalZ)<br>
//	 * 1 - Sphere(radius)<br>
//	 * 2 - Box(sizeX, sizeY, sizeZ)<br>
//	 * 3 - Cylinder(halfExtendX, halfExtendY, halfExtendZ)<br>
//	 * 4 - Capsule(radius, height)<br>
//	 * 5 - Cone(radius, height)<br>
//	 * 6 - #TODO<br>
//	 * 7 - ConvexHullShape(List:Vector3f: points)<br>
//	 * 8 - TriangleMeshShape(float[] vert, int[] indices)<br>
//	 */
//	public PEntity(GEntity gentity, float mass, int type, Object...shapeArg) {
//		v(gentity, mass, type, shapeArg);
//	}
//
//	private void v(GEntity gentity, float mass, int type, Object...shapeArg) {
//		this.type = type;
//		this.args = new Object[3];
//		System.arraycopy(shapeArg, 0, this.args, 0, shapeArg.length);
//		this.gentity = gentity;
//		this.gentity.setProperty("Physics", this);
//		if(type == 0) {
//			collShape = new StaticPlaneShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]), 1);
//		}
//		else if(type == 1) {
//			collShape = new SphereShape((float)shapeArg[0]);
//		}
//		else if(type == 2) {
//			collShape = new BoxShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
//		}
//		else if(type == 3) {
//			collShape = new CylinderShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
//		}
//		else if(type == 4) {
//			collShape = new CapsuleShape((float)shapeArg[0], (float)shapeArg[1]);
//		}
//		else if(type == 5) {
//			collShape = new ConeShape((float)shapeArg[0], (float)shapeArg[1]);
//		}
//		else if(type == 6) {
//			collShape = new CompoundShape();//TODO
//		}
//		else if(type == 8) {
//			float[] vertices = (float[]) shapeArg[0];
//			int[] indices = (int[]) shapeArg[1];
//			ByteBuffer ver = ByteBuffer.allocate(vertices.length*4);
//			ver.asFloatBuffer().put(vertices);
//			ByteBuffer ind = ByteBuffer.allocate(indices.length*4);
//			ind.asIntBuffer().put(indices);
//			IndexedMesh im = new IndexedMesh();
//			
//			im.numTriangles = indices.length/3;
//			im.numVertices = vertices.length;
//			im.triangleIndexStride = 3*4;
//			im.vertexStride = 3*4;
//			im.triangleIndexBase = ind;
//			im.vertexBase = ver;
//			TriangleIndexVertexArray t = new TriangleIndexVertexArray();
//			t.addIndexedMesh(im);
//			collShape = new BvhTriangleMeshShape(t, true);
//			keepAlive.add(ver);
//			keepAlive.add(ind);
//		}
//		else {
//			throw new RuntimeException("type not detected");
//		}
//		org.joml.Matrix4f m = new org.joml.Matrix4f();
//		m.rotateXYZ(gentity.getRotation());
//		Quaternionf q = m.getNormalizedRotation(new Quaternionf());
//		motionState = new DefaultMotionState(trans = new Transform(new Matrix4f(new Quat4f(q.x, q.y, q.z, q.w), new Vector3f(gentity.getPosition().x, gentity.getPosition().y, gentity.getPosition().z), 1.0f)));
//		Vector3f v = new Vector3f();
//		collShape.calculateLocalInertia(mass, v);
//		info = new RigidBodyConstructionInfo(mass, motionState, collShape, v);
//		body = new RigidBody(info);
//	}
//	
//	/**
//	 * Sets the collider for this body.<br>
//	 * Types:<br>
//	 * (Arguments as floats)<br>
//	 * 0 - Static Plane(normalX, normalY, normalZ)<br>
//	 * 1 - Sphere(radius)<br>
//	 * 2 - Box(sizeX, sizeY, sizeZ)<br>
//	 * 3 - Cylinder(halfExtendX, halfExtendY, halfExtendZ)<br>
//	 * 4 - Capsule(radius, height)<br>
//	 * 5 - Cone(radius, height)<br>
//	 * 6 - #TODO<br>
//	 * 7 - ConvexHullShape(List:Vector3f: points)<br>
//	 * 8 - TriangleMeshShape(float[] vert, int[] indices)<br>
//	 * @param type
//	 * @param shapeArg
//	 */
//	public void setCollision(int type, Object...shapeArg) {
//		this.type = type;
//		this.args = shapeArg;
//		this.gentity.setProperty("Physics", this);
//		if(type == 0) {
//			collShape = new StaticPlaneShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]), 1);
//		}
//		else if(type == 1) {
//			collShape = new SphereShape((float)shapeArg[0]);
//		}
//		else if(type == 2) {
//			collShape = new BoxShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
//		}
//		else if(type == 3) {
//			collShape = new CylinderShape(new Vector3f((float)shapeArg[0], (float)shapeArg[1], (float)shapeArg[2]));
//		}
//		else if(type == 4) {
//			collShape = new CapsuleShape((float)shapeArg[0], (float)shapeArg[1]);
//		}
//		else if(type == 5) {
//			collShape = new ConeShape((float)shapeArg[0], (float)shapeArg[1]);
//		}
//		else if(type == 6) {
//			collShape = new CompoundShape();//TODO
//		}
//		else if(type == 8) {
//			float[] vertices = (float[]) shapeArg[0];
//			int[] indices = (int[]) shapeArg[1];
//			ByteBuffer ver = ByteBuffer.allocate(vertices.length*4);
//			ver.asFloatBuffer().put(vertices);
//			ByteBuffer ind = ByteBuffer.allocate(indices.length*4);
//			ind.asIntBuffer().put(indices);
//			IndexedMesh im = new IndexedMesh();
//			
//			im.numTriangles = indices.length/3;
//			im.numVertices = vertices.length;
//			im.triangleIndexStride = 3*4;
//			im.vertexStride = 3*4;
//			im.triangleIndexBase = ind;
//			im.vertexBase = ver;
//			TriangleIndexVertexArray t = new TriangleIndexVertexArray();
//			t.addIndexedMesh(im);
//			collShape = new BvhTriangleMeshShape(t, true);
//			keepAlive.add(ver);
//			keepAlive.add(ind);
//		}
//		else {
//			throw new RuntimeException("type not detected");
//		}
//		body.setCollisionShape(collShape);
//		body.updateInertiaTensor();
//	}
//	
//	/**
//	 *Synchronizes the P- and the GEntity
//	 */
//	void update() {
//		if(!isStatic) {
//			this.gentity.setUpdate(false); //Matrix is set directly by JBullet
//			float[] data = new float[16];
//			body.getCenterOfMassTransform(trans).getOpenGLMatrix(data);
//			gentity.getTransformationMatrix().set(data);
//			gentity.getTransformationMatrix().scale(gentity.getScale());
//			gentity.getPosition().set(getPosition());
//			gentity.getRotation().set(getRotation());
//		}
//		else {
//			setPosition(gentity.getPosition().x, gentity.getPosition().y, gentity.getPosition().z);
//			setRotation(gentity.getRotation().x, gentity.getRotation().y, gentity.getRotation().z);
//		}
//	}
//	/**
//	 * Returns the GEntity
//	 * @return
//	 */
//	public GEntity getGentity() {
//		return gentity;
//	}
//	/**
//	 * Sets the GEntity.
//	 * @param gentity
//	 * @return
//	 */
//	public PEntity setGentity(GEntity gentity) {
//		this.gentity = gentity;
//		return this;
//	}
//	/**
//	 * Returns the Bounding box of the collider
//	 * @return
//	 */
//	public AABB getAABB() {
//		Vector3f m = new Vector3f();
//		Vector3f mm = new Vector3f();
//		body.getAabb(m, mm);
//		return new AABB(m.x, m.y, m.z, mm.x, mm.y, mm.z);
//	}
//	/**
//	 * Sets the position of the PEntity
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity setPosition(float x, float y, float z) {
//		body.activate();
//		trans.origin.x = x;
//		trans.origin.y = y;
//		trans.origin.z = z;
//		body.getMotionState().setWorldTransform(trans);
//		body.setCenterOfMassTransform(trans);
//		return this;
//	}
//	/**
//	 * Returns the position of the PEntity
//	 * @return
//	 */
//	public org.joml.Vector3f getPosition() {
//		body.activate();
//		Vector3f v = new Vector3f();
//		body.getCenterOfMassPosition(v);
//		return new org.joml.Vector3f(v.x, v.y, v.z);
//	}
//	/**
//	 * Restrains an axis to the given value.<br>
//	 * axis:<br>
//	 *   0 == x<br>
//	 *   1 == y<br>
//	 *   2 == z<br>
//	 * @param axis
//	 * @param value
//	 * @return
//	 */
//	public PEntity restrainPosition(int axis, float value) {
//		body.activate();
//		if(axis == 0)
//			trans.origin.x = value;
//		else if(axis == 1)
//			trans.origin.y = value;
//		else
//			trans.origin.z = value;
//		body.getMotionState().setWorldTransform(trans);
//		body.setCenterOfMassTransform(trans);
//		return this;
//	}
//	/**
//	 * Returns the rotation
//	 * @return rotation in degrees
//	 */
//	public org.joml.Vector3f getRotation() {
//		body.activate();
//		Quat4f r = new Quat4f();
//		body.getCenterOfMassTransform(trans);
//		trans.getRotation(r);
//		Quaternionf q = new Quaternionf(r.x, r.y, r.z, r.w);
//		return q.getEulerAnglesXYZ(new org.joml.Vector3f()).mul(57.29577951308232f);
//	}
//	/**
//	 * Sets the rotation
//	 * @param x in degrees
//	 * @param y in degrees
//	 * @param z in degrees
//	 * @return
//	 */
//	public PEntity setRotation(float x, float y, float z) {
//		body.activate();
//		Quaternionf q = new Quaternionf();
//		q.setFromNormalized(mat.identity().rotateXYZ((float)Math.toRadians(x), (float)Math.toRadians(y), (float)Math.toRadians(z)));
//		Quat4f r = new Quat4f(q.x, q.y, q.z, q.w);
//		trans.setRotation(r);
//		body.getMotionState().setWorldTransform(trans);
//		body.setCenterOfMassTransform(trans);
//		return this;
//	}
//	/**
//	 * Restrains an axis to the given value.<br>
//	 * axis:<br>
//	 *   0 == x<br>
//	 *   1 == y<br>
//	 *   2 == z<br>
//	 * @param axis
//	 * @param value in degrees
//	 * @return
//	 */
//	public PEntity restrainRotation(int axis, float value) {
//		body.activate();
//		org.joml.Vector3f rot = getRotation();
//		if(axis == 0)
//			rot.x = (float)Math.toRadians(axis);
//		else if(axis == 1)
//			rot.y = (float)Math.toRadians(axis);
//		else
//			rot.z = (float)Math.toRadians(axis);
//		setRotation(rot.x, rot.y, rot.z);
//		return this;
//	}
//	
//	/**
//	 * Applies a central force onto the body
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity applyCentralForce(float x, float y, float z) {
//		body.activate();
//		body.applyCentralForce(new Vector3f(x, y, z));
//		return this;
//	}
//	/**
//	 * Applies a central Impulse
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity applyCentralImpulse(float x, float y, float z) {
//		body.activate();
//		body.applyCentralImpulse(new Vector3f(x, y, z));
//		return this;
//	}
//	/**
//	 * Applies a force relative to the center
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @param rx
//	 * @param ry
//	 * @param rz
//	 * @return
//	 */
//	public PEntity applyForce(float x, float y, float z, float rx, float ry, float rz) {
//		body.activate();
//		body.applyForce(new Vector3f(x, y, z), new Vector3f(rx, ry, rz));
//		return this;
//	}
//	/**
//	 * Applies an impulse relative to the center
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @param rx
//	 * @param ry
//	 * @param rz
//	 * @return
//	 */
//	public PEntity applyImpulse(float x, float y, float z, float rx, float ry, float rz) {
//		body.activate();
//		body.applyImpulse(new Vector3f(x, y, z), new Vector3f(rx, ry, rz));
//		return this;
//	}
//	
//	/**
//	 * Applies torque.
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity applyTorque(float x, float y, float z) {
//		body.activate();
//		body.applyTorque(new Vector3f(x, y, z));
//		return this;
//	}
//	/**
//	 * Applies a torque impulse
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity applyTorqueImpulse(float x, float y, float z) {
//		body.activate();
//		body.applyTorqueImpulse(new Vector3f(x, y, z));
//		return this;
//	}
//	
//	/**
//	 * Sets the motion
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity setPositionMotion(float x, float y, float z) {
//		body.activate();
//		body.setLinearVelocity(new Vector3f(x, y, z));
//		return this;
//	}
//	/**
//	 * Sets the rotational motion
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @return
//	 */
//	public PEntity setRotationMotion(float x, float y, float z) {
//		body.activate();
//		body.setAngularVelocity(new Vector3f(x, y, z));
//		return this;
//	}
//	/**
//	 * Returns the motion
//	 * @return
//	 */
//	public org.joml.Vector3f getPositionMotion() {
//		body.activate();
//		Vector3f v = new Vector3f();
//		body.getLinearVelocity(v);
//		return new org.joml.Vector3f(v.x, v.y, v.z);
//	}
//	/**
//	 * Returns the rotational motion
//	 * @return
//	 */
//	public org.joml.Vector3f getRotationMotion() {
//		body.activate();
//		Vector3f v = new Vector3f();
//		body.getAngularVelocity(v);
//		return new org.joml.Vector3f(v.x, v.y, v.z);
//	}
//	/**
//	 * Activates the body
//	 */
//	public void activate() {
//		body.activate();
//	}
//	/**
//	 * Sets this body to be a character
//	 */
//	public void setCharacter() {
//		body.setSleepingThresholds(0, 0);
//		body.setAngularFactor(0);
//	}
//	/**
//	 * Sets the mass of the body
//	 * @param mass
//	 * @return
//	 */
//	public PEntity setMass(float mass) {
//		Vector3f v = new Vector3f();
//		collShape.calculateLocalInertia(mass, v);
//		body.setMassProps(mass, v);
//		return this;
//	}
//	/**
//	 * Returns the mass of the body
//	 * @return
//	 */
//	public float getMass() {
//		return 1/body.getInvMass();
//	}
//	/**
//	 * Sets this body to be kinematic
//	 * @return
//	 */
//	public PEntity setKinematic() {
//		body.setCollisionFlags(CollisionFlags.KINEMATIC_OBJECT);
//		body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
//		body.setMotionState(new kms());
//		return this;
//	}
//	/*
//	 * Sets the activation state
//	 */
//	public PEntity setActivationState(int state) {
//		body.forceActivationState(state);
//		return this;
//	}
//	/**
//	 * Returns the activation state
//	 * @return
//	 */
//	public int getActivationState() {
//		return body.getActivationState();
//	}
//	/**
//	 * Sets the collision flags
//	 * @param flag
//	 * @return
//	 */
//	public PEntity setCollisionFlag(int flag) {
//		body.setCollisionFlags(flag == 3 ? 8 : flag == 4 ? 16 : flag);
//		return this;
//	}
//	
//	class kms extends MotionState{
//		public Transform worldTransform = new Transform();
//		{
//			worldTransform.setIdentity();
//		}
//		@Override
//	    public Transform getWorldTransform(Transform out) {
//	        out.set(worldTransform);
//	        return out;
//	    }
//
//	    @Override
//	    public void setWorldTransform(Transform worldTrans) {
//	        worldTransform.set(worldTrans);
//	    }
//		
//	}
//	/**
//	 * Returns the type of the body
//	 * @return
//	 */
//	public int getType() {
//		return type;
//	}
//	/**
//	 * Returns the Arguments used to generate the body
//	 * @return
//	 */
//	public Object[] getArgs() {
//		return args;
//	}
//	/**
//	 * Sets the friction
//	 * @param friction
//	 * @return
//	 */
//	public PEntity setFriction(float friction) {
//		body.setFriction(friction);
//		return this;
//	}
//	/**
//	 * Returns the friction
//	 * @return
//	 */
//	public float getFriction() {
//		return body.getFriction();
//	}
//	/**
//	 * Returns the collision shape
//	 * @return
//	 */
//	public CollisionShape getCollShape() {
//		return collShape;
//	}
//	/**
//	 * Sets the collision shape
//	 * @param collShape
//	 * @return
//	 */
//	public PEntity setCollShape(CollisionShape collShape) {
//		this.collShape = collShape;
//		return this;
//	}
//	/**
//	 * Returns the Construction info of the body
//	 * @return
//	 */
//	public RigidBodyConstructionInfo getInfo() {
//		return info;
//	}
//	/**
//	 * Returns the rigidbody
//	 * @return
//	 */
//	public RigidBody getBody() {
//		return body;
//	}
//
//	/**
//	 * Returns the current motion state
//	 * @return
//	 */
//	public MotionState getMotionState() {
//		return motionState;
//	}
//	/**
//	 * Returns the bodys transform
//	 * @return
//	 */
//	public Transform getTrans() {
//		return trans;
//	}
//	/**
//	 * Returns if the body is static. Static PEntities will, instead of passing their own transform to the GEntity, take the transform of the GEntity.
//	 * @return
//	 */
//	public boolean isStatic() {
//		return isStatic;
//	}
//	/**
//	 * Sets the body to be static. Static PEntities will, instead of passing their own transform to the GEntity, take the transform of the GEntity.
//	 * @param isStatic
//	 * @return
//	 */
//	public PEntity setStatic(boolean isStatic) {
//		this.isStatic = isStatic;
//		return this;
//	}
//	
}
