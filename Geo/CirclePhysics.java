package Kartoffel.Licht.Geo;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class CirclePhysics {
	
	public static Vector3f gravity = new Vector3f(0, -10, 0);

	public static class actor {
		public Vector3f position3f = new Vector3f();
		public Vector2f position2f = new Vector2f();
		public Vector3f velocity = new Vector3f();
		public void setPosition(float x, float y, float z) {
			position3f.set(x, y, z);
			position2f.set(x, y);
		}
		public float radius = 1;
		public boolean bstatic = false;
	}
	
	public static List<actor> actors = new ArrayList<>();
	
	public static void update(float delta) {
		for(actor a : actors) {
			if(!a.bstatic)
				a.velocity.add(gravity.x*delta, gravity.y*delta, gravity.z*delta);
		}
		for(actor a : actors) {
			
			for(actor b : actors) {
				if(a == b)
					continue;
				if(a.bstatic)
					continue;
				collide(a, b, delta);
			}
		}
		
		//Updating
		for(actor a : actors) {
			a.position3f.add(a.velocity.x*delta, a.velocity.y*delta, a.velocity.z*delta);
			a.position2f.set(a.position3f.x, a.position3f.y);
		}
	}
	
	public static void collide(actor a, actor b, float delta) {
		Vector3f v = new Vector3f();
		Vector3f norm = new Vector3f();
		v.set(b.position3f.x+b.velocity.x-a.position3f.x-a.velocity.x, b.position3f.y+b.velocity.y-a.position3f.y-a.velocity.y, b.position3f.z+b.velocity.z-a.position3f.z-a.velocity.z);
		float dist = v.length();
		float r = a.radius+b.radius;
		float repulsion = r-dist;
		float force = a.velocity.distance(b.velocity);
		v.normalize();
		float dirMul = v.dot(a.velocity.normalize(norm));
		if(repulsion >= 1) {
			v.mul(-force*dirMul-repulsion*0.01f);
			a.velocity.add(v);
		}
	}

}
