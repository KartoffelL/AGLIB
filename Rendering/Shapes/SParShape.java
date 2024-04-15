package Kartoffel.Licht.Rendering.Shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.util.par.ParShapes;
import org.lwjgl.util.par.ParShapesMesh;

public class SParShape extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3813169513610551395L;
	public SParShape(ParShapesMesh mesh) {
		FloatBuffer fb = mesh.points(mesh.npoints()*3);
		this.ver = new float[mesh.npoints()*3];
		fb.get(this.ver);
		IntBuffer in = mesh.triangles(mesh.ntriangles()*3);
		this.ind = new int[mesh.ntriangles()*3];
		in.get(this.ind);
		FloatBuffer nm = mesh.normals(mesh.npoints()*3);
		this.nor = new float[mesh.npoints()*3];
		if(nm != null)
			nm.get(this.nor);
		FloatBuffer tx = mesh.tcoords(mesh.npoints()*2);
		this.tex = new float[mesh.npoints()*2];
		if(tx != null)
			tx.get(this.tex);
		mesh.free();
	}
	
	public static SParShape cone(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_cone(slices, stacks));
	}
	public static SParShape cube() {
		return new SParShape(ParShapes.par_shapes_create_cube());
	}
	public static SParShape cylinder(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_cylinder(slices, stacks));
	}
	public static SParShape disk(float radius, int slices, float centerX, float centerY, float centerZ, float normalX, float normalY, float normalZ) {
		return new SParShape(ParShapes.par_shapes_create_disk(radius, slices, new float[] {centerX, centerY, centerZ}, new float[] {normalX, normalY, normalZ}));
	}
	public static SParShape dodecahedron() {
		return new SParShape(ParShapes.par_shapes_create_dodecahedron());
	}
	public static SParShape hemisphere(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_hemisphere(slices, stacks));
	}
	public static SParShape icosahedron() {
		return new SParShape(ParShapes.par_shapes_create_icosahedron());
	}
	public static SParShape klein_bottle(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_klein_bottle(slices, stacks));
	}
	public static SParShape octahedron() {
		return new SParShape(ParShapes.par_shapes_create_octahedron());
	}
	public static SParShape parametric_disk(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_parametric_disk(slices, stacks));
	}
	public static SParShape parametric_sphere(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_parametric_sphere(slices, stacks));
	}
	public static SParShape plane(int slices, int stacks) {
		return new SParShape(ParShapes.par_shapes_create_plane(slices, stacks));
	}
	public static SParShape rock(int seed, int subdivisions) {
		return new SParShape(ParShapes.par_shapes_create_rock(seed, subdivisions));
	}
	public static SParShape subdivided_sphere(int subdivisions) {
		return new SParShape(ParShapes.par_shapes_create_subdivided_sphere(subdivisions));
	}
	public static SParShape tetrahedron() {
		return new SParShape(ParShapes.par_shapes_create_tetrahedron());
	}
	public static SParShape torus(int slices, int stacks, float radius) {
		return new SParShape(ParShapes.par_shapes_create_torus(slices, stacks, radius));
	}
	public static SParShape trefoil_know(int slices, int stacks, float radius) {
		return new SParShape(ParShapes.par_shapes_create_trefoil_knot(slices, stacks, radius));
	}
}
