package Kartoffel.Licht.Rendering.Shapes;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.par.ParOctasphere;
import org.lwjgl.util.par.ParOctasphereConfig;
import org.lwjgl.util.par.ParOctasphereMesh;

public class SSphere extends Shape{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 937100979800614377L;

	public SSphere(float width, float height, float depth, float radius, int subdivisions) {
		ByteBuffer b = ByteBuffer.allocateDirect(28);
		ParOctasphereConfig cfg = new ParOctasphereConfig(b);
		cfg.corner_radius(radius);
		cfg.width(width);
		cfg.height(height);
		cfg.depth(depth);
		cfg.num_subdivisions(subdivisions);
		int[] num_vertices = new int[1];
		int[] num_indices = new int[1];
		ParOctasphere.par_octasphere_get_counts(cfg, num_indices, num_vertices);
		
		ByteBuffer b2 = ByteBuffer.allocateDirect(40);
		ParOctasphereMesh mesh = new ParOctasphereMesh(b2);
		
		FloatBuffer data = MemoryUtil.memAllocFloat(num_vertices[0]*3);
		mesh.positions(data);
		
		FloatBuffer data4 = MemoryUtil.memAllocFloat(num_vertices[0]*3);
		mesh.normals(data4);
		
		FloatBuffer data3 = MemoryUtil.memAllocFloat(num_vertices[0]*2);
		mesh.texcoords(data3);
		
		ShortBuffer data2 = MemoryUtil.memAllocShort(num_indices[0]);
		mesh.indices(data2);
		
		ParOctasphere.par_octasphere_populate(cfg, mesh);
		
		
		ver = new float[mesh.num_vertices()*3];
		data.get(ver);
		
		tex = new float[mesh.num_vertices()*2];
		data3.get(tex);
		
		nor = new float[mesh.num_vertices()*3];
		data4.get(nor);
		
		
		short[] in = new short[mesh.num_indices()];
		data2.get(in);
		this.ind = new int[in.length];
		for(int i = 0; i < in.length; i++) {
			ind[i] = in[i];
		}
		
	}
	

}
