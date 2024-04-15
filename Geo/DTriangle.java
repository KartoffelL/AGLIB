package Kartoffel.Licht.Geo;

import java.util.Random;

import org.joml.Vector3d;

public class DTriangle implements DefinableShape{

	public double x1, y1, z1, x2, y2, z2, x3, y3, z3;
	
	public DTriangle() {
		
	}
	
	public DTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.x3 = x3;
		this.y3 = y3;
		this.z3 = z3;
	}
	
	public DTriangle(Vector3d a, Vector3d b, Vector3d c) {
		super();
		this.x1 = a.x;
		this.y1 = a.y;
		this.z1 = a.z;
		this.x2 = b.x;
		this.y2 = b.y;
		this.z2 = b.z;
		this.x3 = c.x;
		this.y3 = c.y;
		this.z3 = c.z;
	}
	//https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm
	private static final double EPSILON = 0.0000001;


	@Override
	public double intersection(Ray r, boolean canBeInside) {
		Vector3d rayVector = new Vector3d(r.dx, r.dy, r.dz);
		Vector3d rayOrigin = new Vector3d(r.x, r.y, r.z);
		Vector3d vertex0 = new Vector3d(x1, y1, z1);
		Vector3d vertex1 = new Vector3d(x2, y2, z2);
		Vector3d vertex2 = new Vector3d(x3, y3, z3);
        Vector3d edge1 = new Vector3d();
        Vector3d edge2 = new Vector3d();
        Vector3d h = new Vector3d();
        Vector3d s = new Vector3d();
        Vector3d q = new Vector3d();
        double a, f, u, v;
        edge1.sub(vertex1, vertex0);
        edge2.sub(vertex2, vertex0);
        h.cross(rayVector, edge2);
        a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON) {
            return Double.POSITIVE_INFINITY;    // This ray is parallel to this triangle.
        }
        f = 1.0 / a;
        s.sub(rayOrigin, vertex0);
        u = f * (s.dot(h));
        if (u < 0.0 || u > 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        q.cross(s, edge1);
        v = f * rayVector.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        // At this stage we can compute t to find out where the intersection point is on the line.
        double t = f * edge2.dot(q);
        if (t > EPSILON) // ray intersection
        {
            return t;
        } else // This means that there is a line intersection but not a ray intersection.
        {
            return Double.POSITIVE_INFINITY;
        }
	}

	@Override
	public Vector3d randomPoint(Random random, Vector3d targed) {
		double a = random.nextDouble();
		double b = random.nextDouble();
		double c = random.nextDouble();
		double abc = a+b+c;
		a /= abc;
		b /= abc;
		c /= abc;
		getBarizentricPoint(targed.set(a, b, c), targed);
		return targed;
	}
	
	public Vector3d getBarizentricPoint(Vector3d barizentric_coords, Vector3d target) {
		target.x = x1*barizentric_coords.x+x2*barizentric_coords.y+x3*barizentric_coords.z;
		target.y = y1*barizentric_coords.y+y2*barizentric_coords.y+y3*barizentric_coords.z;
		target.z = z1*barizentric_coords.z+z2*barizentric_coords.y+z3*barizentric_coords.z;
		return target;
		
	}

	@Override
	public AABB getBoundingBox() {
		return new AABB(
				Math.min(x1, Math.min(x2, x3)), Math.min(y1, Math.min(y2, y3)), Math.min(y1, Math.min(y2, y3)),
				Math.max(x1, Math.max(x2, x3)), Math.max(y1, Math.max(y2, y3)), Math.max(y1, Math.max(y2, y3))
				);
	}
	
	@Override
	public DTriangle clone() {
		return new DTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3);
	}
	

}
