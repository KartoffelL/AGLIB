package Kartoffel.Licht.Geo.LibBulletJme;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.joml.Vector3d;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.joints.New6Dof;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.system.NativeLibraryLoader;

import Kartoffel.Licht.Geo.Ray;
import Kartoffel.Licht.Res.Downloader;



//Reference: https://stephengold.github.io/Libbulletjme/lbj-en/English/new6dof.html
public class Physics {
	
	static {
		PhysicsRigidBody.logger2.setLevel(Level.OFF);
		NativeLibraryLoader.logger.setLevel(Level.OFF);
		New6Dof.logger2.setLevel(Level.OFF);
	}
	
	public static final list entitites = new list();
	
	public static PhysicsSpace pspace;
	
	public static void init(int numThreads, boolean doublePer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		try {
			String OS = System.getProperty("os.name");
			OS = OS.startsWith("Windows") ? "Windows" : OS.startsWith("Mac") ? "MacOSX" : "Linux";
			Downloader.downloadNativeLibrary_JBULLET("18.5.2", OS, System.getProperty("os.arch").contains("64") ? 64 : 32, true, doublePer, numThreads == 1 ? false : true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pspace = new PhysicsSpace(new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ), BroadphaseType.DBVT, numThreads);
	}
	
	public static void free() {
		pspace.destroy();
	}
	
	public static void setMaxSubStep(int i) {
		pspace.setMaxSubSteps(i);
	}
	public static void setMaxTimeSetp(float t) {
		pspace.setMaxTimeStep(t);
	}
	public static void setAccuracy(float a) {
		pspace.setAccuracy(a);
	}

	public static void update(float delta) {
		for(PEntity e : entitites.getRawList())
			e.update();
		pspace.update(delta);
	}
	
	static void add(PEntity entity) {
		pspace.addCollisionObject(entity.getBody());
	}
	static void remove(PEntity entity) {
		pspace.remove(entity.getBody());
	}
	
	public static float scale = 1000;
	/**
	 * Collision detection using Bullet.
	 * @param ray
	 * @param canBeInside
	 * @return HitInfo
	 */
	public static List<HitInfo> intersectionBullet(Ray ray) {
		List<PhysicsRayTestResult> l = pspace.rayTest(new com.jme3.math.Vector3f((float)ray.x, (float)ray.y, (float)ray.z), new com.jme3.math.Vector3f((float)(ray.x+ray.dx*scale), (float)(ray.y+ray.dy*scale), (float)(ray.z+ray.dz*scale)));
		List<HitInfo> info = new ArrayList<>();
		for(PhysicsRayTestResult p : l) {
			HitInfo i = new HitInfo();
			float hitf = p.getHitFraction();
			i.hitFraction = hitf;
			i.distance = scale*hitf;
			i.pos = new Vector3d(ray.x+ray.dx*i.distance, ray.y+ray.dy*i.distance, ray.z+ray.dz*i.distance);
			com.jme3.math.Vector3f v = new com.jme3.math.Vector3f();
			p.getHitNormalLocal(v);
			i.normal = new Vector3d(v.x, v.y, v.z);
			PEntity e = null;
			for(PEntity ee : entitites.getRawList()) {
				if(ee.getBody() == p.getCollisionObject())
					e = ee;
			}
			i.entity = e;
			info.add(i);
		}
		return info;
	}
	/**
	 * Collision detection using Bullet.
	 * @param ray
	 * @param canBeInside
	 * @return HitInfo
	 */
	public static HitInfo intersectionBulletNearest(Ray ray) {
		List<HitInfo> i = intersectionBullet(ray);
		if(i.size() != 0)
			return i.get(0);
		return new HitInfo();
	}
	
	public static class HitInfo {
		public Vector3d pos = null;
		public Vector3d normal = null;
		public PEntity entity = null;
		public float hitFraction = 1;
		public float distance = Float.POSITIVE_INFINITY;
	}
	
	public static void setGravity(float x, float y, float z) {
		pspace.setGravity(new com.jme3.math.Vector3f(x, y, z));
	}
	private static com.jme3.math.Vector3f v = new com.jme3.math.Vector3f();
	public static org.joml.Vector3f getGravity() {
		pspace.getGravity(v);
		return new org.joml.Vector3f(v.x, v.y, v.z);
	}

}
