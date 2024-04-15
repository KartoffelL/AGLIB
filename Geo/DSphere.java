package Kartoffel.Licht.Geo;

import java.util.Random;

import org.joml.Vector3d;

public class DSphere implements DefinableShape{

	public double x, y, z, radius;
	
	public DSphere() {
		
	}

	public DSphere(double x, double y, double z, double radius) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
	}
	
	
	
	@Override
	public double intersection(Ray r, boolean canBeInside) {
		double a = new Vector3d(r.dx, r.dy, r.dz).dot(r.dx, r.dy, r.dz);
		double b = 2 * new Vector3d(r.x-x, r.y-y, r.z-z).dot(r.dx, r.dy, r.dz);
		double c = new Vector3d(r.x-x, r.y-y, r.z-z).dot(r.x-x, r.y-y, r.z-z) - radius*radius;
		double discriminant = b*b - 4*a*c;
		if(discriminant < 0){
	        return Double.POSITIVE_INFINITY;
	    }
	    else{
	        return (-b - Math.sqrt(discriminant)) / (2.0*a);
	    }
	}

	@Override
	public Vector3d randomPoint(Random random, Vector3d targed) {
		targed.set(radius, 0, 0);
		targed.rotateX(random.nextDouble(Math.PI*2));
		targed.rotateY(random.nextDouble(Math.PI*2));
		targed.rotateZ(random.nextDouble(Math.PI*2));
		targed.add(x, y, z);
		return targed;
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+","+z+"|"+radius+"]";
	}

	@Override
	public AABB getBoundingBox() {
		return new AABB(x-radius, y-radius, z-radius, x+radius, y+radius, z+radius);
	}
	
	@Override
	public DSphere clone() {
		return new DSphere(x, y, z, radius);
	}

}
