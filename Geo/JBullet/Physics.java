package Kartoffel.Licht.Geo.JBullet;

//Using cz.advel.jbullet
@Deprecated
public class Physics {
//	
//	public static final list entitites = new list();
//	
//	public static BroadphaseInterface broadphase;
//	public static DefaultCollisionConfiguration collisionConfiguration;
//	public static CollisionDispatcher dispatcher;
//	
//	public static SequentialImpulseConstraintSolver solver;
//	
//	public static DiscreteDynamicsWorld dynamicsWorld;
//	
//	private static Thread phisicsThread;
//	private static volatile boolean running;
//	private static volatile boolean multithreaded = false;
//	private static volatile List<objectChange> to = new ArrayList<>();
//	public static volatile double delta = 0;
//	private static volatile double deltaMul = 1;
//	public static String lastError = "";
//	public static int maxSubSteps = 10;
//	
//	public static void init(boolean multithreaded) { //Init#
//		broadphase = new DbvtBroadphase();
//		solver = new SequentialImpulseConstraintSolver();
//		collisionConfiguration = new DefaultCollisionConfiguration();
//		dispatcher = new CollisionDispatcher(collisionConfiguration);
//		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
//		dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
//		running = true;
//		Physics.multithreaded = multithreaded;
//		if(multithreaded) {
//			phisicsThread = new Thread() {
//				@Override
//				public void run() {
//					while(running)
//						try {
//							while(running) {
//								u();
//							}
//						} catch (Throwable e) {
//							lastError = e.getMessage();
//						}
//				}
//			};
//			phisicsThread.start();
//		}
//	}
//	
//	private static void u() {
//		Timer.startTimer(1001);
//		dynamicsWorld.stepSimulation((float) (delta*deltaMul), maxSubSteps);
//		for(int i = 0; i < to.size(); i++) {
//			objectChange o = to.get(0);
//			if(o.command == 0)
//				if(o.group != -1)
//					dynamicsWorld.addRigidBody(o.affected, o.group, o.mask);
//				else
//					dynamicsWorld.addRigidBody(o.affected);
//			else if(o.command == 1)
//				dynamicsWorld.removeRigidBody(o.affected);
//			to.remove(0);
//		}
//		delta = Timer.stopTimer(1001);
//	
//	}
//	
//	public static void free() {
//		running = false;
//	}
//	
//	public static void setDeltaMul(double deltaMul) {
//		Physics.deltaMul = deltaMul;
//	}
//
//	public static void update() {
//		for(PEntity e : entitites.getRawList())
//			e.update();
//	}
//	
//	public static void updateAll(double delta) {
//		if(!multithreaded && running)
//			try {
//				Physics.delta = delta;
//				u();
//			} catch (Throwable e) {
//				lastError = e.getMessage();
//			}
//		for(PEntity e : entitites.getRawList())
//			e.update();
//	}
//	
//	static void add(PEntity entity) {
//		to.add(new objectChange(entity.getBody(), 0, (short)-1, (short)-1));
//	}
//	static void add(PEntity entity, short group, short mask) {
//		to.add(new objectChange(entity.getBody(), 0, group, mask));
//	}
//	static void remove(PEntity entity) {
//		to.add(new objectChange(entity.getBody(), 1, (short)-1, (short)-1));
//	}
//	public static float scale = 1000;
//	/**
//	 * Collision detection using JBullet.
//	 * @param ray
//	 * @param canBeInside
//	 * @return HitInfo
//	 */
//	public static HitInfo intersectionJBullet(Ray ray, boolean canBeInside) {
//		CollisionWorld.ClosestRayResultCallback call = new CollisionWorld.ClosestRayResultCallback(new Vector3f((float)ray.x, (float)ray.y, (float)ray.z),
//				new Vector3f((float)ray.x+(float)ray.dx*scale, (float)ray.y+(float)ray.dy*scale, (float)ray.z+(float)ray.dz*scale));
//		dynamicsWorld.rayTest(call.rayFromWorld, call.rayToWorld, call);
//		HitInfo h = new HitInfo();
//		h.pos = new Vector3d(
//				call.hasHit() ? call.rayFromWorld.x+ray.dx*call.closestHitFraction*scale : Double.POSITIVE_INFINITY,
//				call.hasHit() ? call.rayFromWorld.y+ray.dy*call.closestHitFraction*scale : Double.POSITIVE_INFINITY,
//				call.hasHit() ? call.rayFromWorld.z+ray.dz*call.closestHitFraction*scale : Double.POSITIVE_INFINITY
//								);
//		h.normal = new Vector3d(call.hitNormalWorld.x, call.hitNormalWorld.y, call.hitNormalWorld.z);
//		for(PEntity e : entitites.getRawList()) {
//			if(e.getBody() == call.collisionObject)
//				h.entity = e;
//		}
//		return h;
//	}
//	
//	public static class HitInfo {
//		public Vector3d pos;
//		public Vector3d normal;
//		public PEntity entity;
//	}
//	
//	public static void setGravity(float x, float y, float z) {
//		dynamicsWorld.setGravity(new Vector3f(x, y, z));
//	}
//	
//	public static org.joml.Vector3f getGravity() {
//		Vector3f v = new Vector3f();
//		dynamicsWorld.getGravity(v);
//		return new org.joml.Vector3f(v.x, v.y, v.z);
//	}
//
}

//
//class objectChange {
//	RigidBody affected;
//	int command;
//	
//	short group;
//	short mask;
//	public objectChange(RigidBody affected, int command, short group, short mask) {
//		super();
//		this.affected = affected;
//		this.command = command;
//		this.group = group;
//		this.mask = mask;
//	}
//	
//	
//}
