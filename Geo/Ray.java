package Kartoffel.Licht.Geo;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

public class Ray {

	public double x, y, z, dx, dy, dz;
	
	
	
	public Ray(double x, double y, double z, double dx, double dy, double dz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}
	
	public Ray() {
		
	}
	
	public Ray(Vector3d position, Vector3d direction) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.dx = direction.x;
		this.dy = direction.y;
		this.dz = direction.z;
	}
	
	public Ray(Vector3f position, Vector3f direction) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.dx = direction.x;
		this.dy = direction.y;
		this.dz = direction.z;
	}
	
	public Ray(Ray ray) {
		this.x = ray.x;
		this.y = ray.y;
		this.z = ray.z;
		this.dx = ray.dx;
		this.dy = ray.dy;
		this.dz = ray.dz;
	}

	public Ray(Vector3f a, Vector4f b) {
		this.x = a.x;
		this.y = a.y;
		this.z = a.z;
		this.dx = b.x;
		this.dy = b.y;
		this.dz = b.z;
	}

	public Ray add(Vector3d v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}
	
	public Ray add(double xx, double yy, double zz) {
		x += xx;
		y += yy;
		z += zz;
		return this;
	}
	
	public Ray sub(Vector3d v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}
	
	public Ray sub(double xx, double yy, double zz) {
		x -= xx;
		y -= yy;
		z -= zz;
		return this;
	}
	
	public Ray mul(Matrix4f tm) {
		Vector4d a = new Vector4d(x, y, z, 1);
		a.mul(tm);
		x = a.x;
		y = a.y;
		z = a.z;
		a = new Vector4d(dx, dy, dz, 1);
		a.mul(tm);
		a.normalize();
		dx = a.x;
		dy = a.y;
		dz = a.z;
		return this;
	}
	
	public Ray equalRotation(Vector3d rotation) {
		return equalRotation(rotation.x, rotation.y, rotation.z);
	}
	
	public Ray equalRotation(double xx, double yy, double zz) {
		Vector3d a = new Vector3d(dx, dy, dz);
		a.rotateX(Math.toRadians(-xx)).rotateY(Math.toRadians(-yy)).rotateZ(Math.toRadians(-zz));
		dx = a.x;
		dy = a.y;
		dz = a.z;
		return this;
	}

	@Override
	public String toString() {
		return "["+x+","+y+","+z+"|"+dx+","+dy+","+dz+"]";
	}

	

}
