package Kartoffel.Licht.Geo;

import java.util.Random;

import org.joml.Vector3d;

public class AABB implements DefinableShape{

	/**
	 * Local coordinates
	 */
	public double x1, y1, z1, x2, y2, z2;
	
	public AABB() {
		
	}

	public AABB(double x1, double y1, double z1, double x2, double y2, double z2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	public boolean intersects(AABB other) {
		return (
			    x1 <= other.x2 &&
			    x2 >= other.x1 &&
			    y1 <= other.y2 &&
			    y2 >= other.y1 &&
			    z1 <= other.z2 &&
			    z2 >= other.z1
			  );
	}
	
	public AABB fix() {
		double xx1 = Math.min(x1, x2);
		double xx2 = Math.max(x1, x2);
		double yy1 = Math.min(y1, y2);
		double yy2 = Math.max(y1, y2);
		double zz1 = Math.min(z1, z2);
		double zz2 = Math.max(z1, z2);
		x1 = xx1;
		x2 = xx2;
		y1 = yy1;
		y2 = yy2;
		z1 = zz1;
		z2 = zz2;
		return this;
	}
	
	public boolean intersects(Ray r, boolean canBeInside) {
		return intersection(r, canBeInside) == Double.POSITIVE_INFINITY;
	}
	
	public AABB add(double xx, double yy, double zz) {
		this.x1 += xx;
		this.x2 += xx;
		this.y1 += yy;
		this.y2 += yy;
		this.z1 += zz;
		this.z2 += zz;
		return this;
	}
	
	public AABB add(Vector3d v) {
		this.x1 += v.x;
		this.x2 += v.x;
		this.y1 += v.y;
		this.y2 += v.y;
		this.z1 += v.z;
		this.z2 += v.z;
		return this;
	}
	
	public AABB add(Vector3d v, AABB dest) {
		return dest.set(
		this.x1 + v.x,
		this.x2 + v.x,
		this.y1 + v.y,
		this.y2 + v.y,
		this.z1 + v.z,
		this.z2 + v.z
		);
	}
	
	public AABB set(double d, double e, double f, double g, double h, double i) {
		this.x1 = d;
		this.x2 = e;
		this.y1 = f;
		this.y2 = g;
		this.z1 = h;
		this.z2 = i;
		return this;
	}

	@Override
	public double intersection(Ray r, boolean canBeInside) {
		double offX = (x1+x2)/2;
		double offY = (y1+y2)/2;
		double offZ = (z1+z2)/2;
		
		double irdx = r.dx == 0 ? 1 : 1/r.dx;
		double irdy = r.dy == 0 ? 1 : 1/r.dy;
		double irdz = r.dz == 0 ? 1 : 1/r.dz;
		double nx = irdx*(r.x-offX);
		double ny = irdy*(r.y-offY);
		double nz = irdz*(r.z-offZ);
		double kx = Math.abs(irdx)*(x2-offX);
		double ky = Math.abs(irdy)*(y2-offY);
		double kz = Math.abs(irdz)*(z2-offZ);
		double t1x = -nx - kx;
		double t1y = -ny - ky;
		double t1z = -nz - kz;
		double t2x = -nx + kx;
		double t2y = -ny + ky;
		double t2z = -nz + kz; 
		double tN = Math.max(Math.max(t1x,  t1y), t1z);
		double tF = Math.min(Math.min(t2x,  t2y), t2z);
		if(tN > tF || tF<0) return Double.POSITIVE_INFINITY; //No Intersection
		if(tN < 0 && !canBeInside) return Double.POSITIVE_INFINITY; //Return false when ray is inside Box
		return tN;
	}

	@Override
	public Vector3d randomPoint(Random random, Vector3d target) {
		//Hollow
//		int i = random.nextInt(3);
//		if(i == 0)
//		{
//			target.x = random.nextDouble(x1, x2);
//			target.y = random.nextDouble(y1, y2);
//			target.z = random.nextBoolean() ? z1 : z2;
//		}
//		else if(i == 1)
//		{
//			target.z = random.nextDouble(z1, z2);
//			target.y = random.nextDouble(y1, y2);
//			target.x = random.nextBoolean() ? x1 : x2;
//		}
//		else if(i == 2)
//		{
//			target.x = random.nextDouble(x1, x2);
//			target.z = random.nextDouble(z1, z2);
//			target.y = random.nextBoolean() ? y1 : y2;
//		}
		target.x = x1 < x2 ? random.nextDouble(x1, x2) : x1;
		target.y = y1 < y2 ? random.nextDouble(y1, y2) : y1;
		target.z = z1 < z2 ? random.nextDouble(z1, z2) : z1;
		return target;
	}
	
	@Override
	public String toString() {
		return "["+x1+","+y1+","+z1+"|"+x2+","+y2+","+z2+"]";
	}

	@Override
	public AABB getBoundingBox() {
		return new AABB().set(x1, x2, y1, y2, z1, z2);
	}

	public Vector3d center() {
		return new Vector3d((x1+x2)/2, (y1+y2)/2, (z1+z2)/2);
	}

	public double width() {
		return x2-x1;
	}
	public double height() {
		return y2-y1;
	}
	public double depth() {
		return z2-z1;
	}
	
	@Override
	public AABB clone() {
		return new AABB(x1, y1, z1, x2, y2, z2);
	}
	
	public static boolean isInBounds(double x, double y, double z) {
		if(Math.abs(x) < 1 && Math.abs(z) < 1 && Math.abs(y) < 1)
			return true;
		return false;
	}
	
	public static boolean isInBounds(double x, double y, double z, float bound) {
		if(Math.abs(x) < bound && Math.abs(z) < bound && Math.abs(y) < bound)
			return true;
		return false;
	}
	
	public static boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2, double x12, double y12, double z12, double x22, double y22, double z22) {
		return (
			    x1 <= x22 &&
			    x2 >= x12 &&
			    y1 <= y22 &&
			    y2 >= y12 &&
			    z1 <= z22 &&
			    z2 >= z12
			  );
	}
	
	public static boolean intersects2D(double x1, double y1, double x2, double y2, double x12, double y12, double x22, double y22) {
		return (
			    x1 <= x22 &&
			    x2 >= x12 &&
			    y1 <= y22 &&
			    y2 >= y12
			  );
	}
	
}
