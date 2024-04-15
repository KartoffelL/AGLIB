package Kartoffel.Licht.Geo;

import java.util.Random;

import org.joml.Vector3d;

public class DPoint implements DefinableShape{

	public double x, y, z;
	
	public DPoint() {
		
	}

	public DPoint(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public double intersection(Ray r, boolean canBeInside) {
		double invLength = org.joml.Math.invsqrt(Math.fma((x-r.x), (x-r.x), Math.fma((y-r.y), (y-r.y), (z-r.z) * (z-r.z))));
		double x2 = (x-r.x) * invLength;
		double y2 = (y-r.y) * invLength;
		double z2 = (z-r.z) * invLength;
        
        invLength = org.joml.Math.invsqrt(Math.fma(r.dx, r.dx, Math.fma(r.dy, r.dy, r.dz * r.dz)));
        double x3 = r.dx * invLength;
        double y3 = r.dy * invLength;
        double z3 = r.dz * invLength;
        
        if(x2 == x3 && y2 == y3 && z2 == z3)
        	return Math.sqrt((x-r.x)*(x-r.x)+(y-r.y)*(y-r.y)+(z-r.z)*(z-r.z));
        
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Vector3d randomPoint(Random random, Vector3d targed) {
		return new Vector3d(x, y, z);
	}
	

	@Override
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}

	@Override
	public AABB getBoundingBox() {
		return new AABB(x, y, z, x, y, z);
	}
	
	@Override
	public DPoint clone() {
		return new DPoint(x, y, z);
	}
	
	

}
