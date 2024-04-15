package Kartoffel.Licht.Rendering;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Geo.AABB;
import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Java.opengl;
import Kartoffel.Licht.Rendering.Shapes.Shape;
import Kartoffel.Licht.Rendering.Shapes.ShapeData;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.AssimpLoader;

/**
 * A Model is a Graphical object, used by the Rendering Thread, for storing Buffers.
 * That means, that it contains all information about Texture coordinates, Vertex positions, normals, etc...
 * Models can be conveniently created using Shapes 
 * 
 * 
 * 
 */
@opengl
public class Model implements freeable, namable{
	
    /** BeginMode */
    public static final int
        GL_POINTS         = 0x0,
        GL_LINES          = 0x1,
        GL_LINE_LOOP      = 0x2,
        GL_LINE_STRIP     = 0x3,
        GL_TRIANGLES      = 0x4,
        GL_TRIANGLE_STRIP = 0x5,
        GL_TRIANGLE_FAN   = 0x6,
        GL_QUADS          = 0x7,
        GL_QUAD_STRIP     = 0x8,
        GL_POLYGON        = 0x9;
    
    /** FillMode */
    public static final int
    	GL_FILLMODE_LINE = 6913,
    	GL_FILLMODE_FILL = 6914,
    	GL_FILLMODE_POINT = 6912;
    
    public static boolean FORCE_DISABLE_FACE_CULLING = false;
    public static int FORCE_POLYGON_MODE = 0;
    
	
    protected String name = "Model";
    
	/**
	 * Every time a model gets drawn, this will increase by one
	 */
	volatile public static int DrawCalls = 0;
	/**
	 * Every time a model gets drawn, this will increase by one
	 */
	volatile public static int DrawElementCalls = 0;
    
	protected int DRAW_TYPE = GL_TRIANGLES;
	protected int FILL_MODE = GL_FILLMODE_FILL;
	
	//Internal stuff#####
	protected boolean Face_Culling_enabled = true;
	protected volatile int ElementDrawCount;
	protected int offset = 0;
	
	protected int material_count;
	protected int vertex_count;
	
	protected HashMap<String, Integer> buffers = new HashMap<String, Integer>();
	//##################
	protected AABB bounds;
	
	/**Instanced used by the rendering Engine. Element array excluded.*/
	public static final int DEFAULT_ATTRIBUTE_COUNT = 6;
	
	/**
	 * Model with only vertices. Nullable(because, why not?)
	 * @param vertices
	 */
	public Model(float[] vertices) {
		MModel(vertices, null, null, null, null, null, null);
	}
	/**
	 * Model with vertices and indices. Nullable
	 * @param vertices
	 * @param ind
	 */
	public Model(float[] vertices, int[] ind) {
		MModel(vertices, null, ind, null, null, null, null);
	}
	/**
	 * Model with vertices and texture coordinates. Nullable
	 * @param vertices
	 * @param tex_coords
	 */
	public Model(float[] vertices, float[] tex_coords) {
		MModel(vertices, tex_coords, null, null, null, null, null);
	}
	/**
	 * Model with vertices, texture coordinates and indices. Nullable
	 * @param vertices
	 * @param tex_coords
	 * @param indices
	 */
	public Model(float[] vertices, float[] tex_coords, int[] indices) {
		MModel(vertices, tex_coords, indices, null, null, null, null);
	}
	/**
	 * Model with optional parameters. Nullable
	 * @param vertices
	 * @param tex_coords
	 * @param normals
	 * @param materials
	 * @param indec
	 */
	public Model(float[] vertices, float[] tex_coords, float[] normals, int[] materials, int[] indec) {
		MModel(vertices, tex_coords, indec, normals, materials, null, null);
	}
	
	private void MModel(float[] vertices, float[] tex_coords, int[] indices, float[] normals, int[] mat, int[] a_bones, float[] a_weights) {
		if(indices != null)
			ElementDrawCount = indices.length;
		else
			ElementDrawCount = 1;
		bounds = new Shape().main(vertices, null, null, null).calcBoundingBox(1).bounds;
		
		int vertex_id = 0;
		if(vertices != null) {
			vertex_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vertex_id);
			glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			vertex_count = vertices.length;
		}
		buffers.put("VERTEX", vertex_id);
		
		int texture_id = 0;
		if(tex_coords != null) {
			texture_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, texture_id);
			glBufferData(GL_ARRAY_BUFFER, tex_coords, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		buffers.put("TEXTURE", texture_id);
		
		int normal_id = 0;
		if(normals != null) {
			normal_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, normal_id);
			glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		buffers.put("NORMAL", normal_id);
		
		int material_id = 0;
		if(mat != null) {
			material_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, material_id);
			glBufferData(GL_ARRAY_BUFFER, mat, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		buffers.put("MATERIAL", material_id);
		
		int indices_id = 0; 
		if(indices != null) {
			indices_id = glGenBuffers();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices_id);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		buffers.put("INDICES", indices_id);
		
		int bones_id = 0;
		if(a_bones != null) {
			bones_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, bones_id);
			glBufferData(GL_ARRAY_BUFFER, a_bones, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		buffers.put("A_BONES", bones_id);
		
		int weights_id = 0;
		if(a_weights != null) {
			weights_id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, weights_id);
			glBufferData(GL_ARRAY_BUFFER, a_weights, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		buffers.put("A_WEIGHTS", weights_id);
		//Ressource("Loaded new Model");
	}

	/**
	 * Creates a new Model using the ShapeData.
	 * @param shape
	 */
	public Model(ShapeData shape) {
		MModel(shape.ver, shape.tex, shape.ind, shape.nor, shape.mat, shape.BoneIds, shape.BoneWeights);
		this.material_count = shape.matl != 0 ? shape.matl : 1;
		this.DRAW_TYPE = shape.drawType == -1 ? GL_TRIANGLES : shape.drawType;
		this.FILL_MODE = shape.fillMode == -1 ? GL_FILLMODE_FILL : shape.fillMode;
	}
	/**
	 * Creates the default Model using the blender Monk
	 * @param material_count
	 */
	public Model() {
		AssimpLoader loader = new AssimpLoader(FileLoader.getFileD("default/models/blender_monk.fbx"));
		MModel(loader.ver, loader.tex, loader.ind, loader.nor, loader.mat, loader.BoneIds, loader.BoneWeights);
		this.material_count = loader.matl;
	}
//	/**
//	 * Sets the Data of the openGL FloatBuffer
//	 * @param data
//	 * @param buffer
//	 */
//	public void setBuffer(float[] data, int buffer) {
//		glBindBuffer(GL_ARRAY_BUFFER, buffer);
//		FloatBuffer fb = createFloatBuffer(data);
//		glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
//		MemoryUtil.memFree(fb);
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//	}
//	/**
//	 * Sets the Data of the openGL IntBuffer
//	 * @param data
//	 * @param buffer
//	 */
//	public void setBufferI(int[] data, int buffer) {
//		glBindBuffer(GL_ARRAY_BUFFER, buffer);
//		IntBuffer ib = createIntBuffer(data);
//		glBufferData(GL_ARRAY_BUFFER, ib, GL_STATIC_DRAW);
//		MemoryUtil.memFree(ib);
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//	}
	/**
	 * Frees all allocated Data on The GPU. Dont forget this when you�d delete a lot of entities and creating a lot of new ones!
	 * This may be the cause of your no Memory Fatal JVM error..maybe..
	 * 
	 */
	public void free() {
		for(int i : buffers.values()) {
			glDeleteBuffers(i);
		}
	}
	/**
	 * Renders the Model with a Element array buffer
	 */
	public void render() {
		DrawCalls++;
		DrawElementCalls += ElementDrawCount;
		if(Face_Culling_enabled && !FORCE_DISABLE_FACE_CULLING)
			glEnable(GL_CULL_FACE);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("VERTEX"));
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("TEXTURE"));
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("NORMAL"));
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("MATERIAL"));
		glVertexAttribPointer(3, 1, GL_UNSIGNED_INT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("A_BONES"));
		glVertexAttribPointer(4, 4, GL_UNSIGNED_INT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, get("A_WEIGHTS"));
		glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, get("INDICES"));
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, FORCE_POLYGON_MODE != 0 ? FORCE_POLYGON_MODE : FILL_MODE);
		glDrawElements(DRAW_TYPE, ElementDrawCount, GL_UNSIGNED_INT, offset);
		
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
		glDisable(GL_CULL_FACE);
	}
	
	/**
	 * Creates a new FloatBuffer from the data
	 * @param data
	 * @return
	 */
	protected FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = MemoryUtil.memAlloc(data.length).asFloatBuffer();
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	/**
	 * Creates a new IntBuffer from the data
	 * @param data
	 * @return
	 */
	protected IntBuffer createIntBuffer(int[] data) {
		IntBuffer buffer = MemoryUtil.memAlloc(data.length).asIntBuffer();
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	/**
	 * Returns the ID of the given Buffer.<br>
	 * 
	 * @param s , one of: VERTEX, TEXTURE, NORMAL, INDICES, MATERIAL
	 * @return the ID, 0 if not found
	 */
	public int get(String s) {
		int i;
		if(buffers.get(s) == null)
			i = 0;
		else
			i = buffers.get(s);
		return i;
	}
	
	/**
	 * The Buffer ID of the Vertex buffer
	 * @return
	 */
	public int getVertex_id() {
		return get("VERTEX");
	}
	/**
	 * The Buffer ID of the Texture buffer
	 * @return
	 */
	public int getTexture_id() {
		return get("TEXTURE");
	}
	/**
	 * The Buffer ID of the Normal buffer
	 * @return
	 */
	public int getNormal_id() {
		return get("NORMAL");
	}
	/**
	 * The Buffer ID of the Indices buffer
	 * @return
	 */
	public int getIndices_id() {
		return get("INDICES");
	}
	/**
	 * The Buffer ID of the Material buffer
	 * @return
	 */
	public int getMaterial_id() {
		return get("Material");
	}
	/**
	 * The count of triangles defined by the Element buffer.
	 * @return
	 */
	public int getElementDrawCount() {
		return ElementDrawCount;
	}
	/**
	 * The offset to where GL should begin to draw
	 * @return
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * If face culling is enabled
	 * @return
	 */
	public boolean isFace_Culling_enabled() {
		return Face_Culling_enabled;
	}
	/**
	 * If faces-culling is enabled, openGL will cull faces which normal vector points away from the camera.<br>
	 * These faces are normally not visible, so this will give a performance boost for large models.<br>
	 * May break models with broken normals, or 2D objects like grass.<br>
	 * Normals are defined by the order of the triangle�s points.
	 * @param face_Culling_enabled
	 */
	public Model setFaceCulling(boolean face_Culling_enabled) {
		Face_Culling_enabled = face_Culling_enabled;
		return this;
	}
	public int getMaterial_count() {
		return material_count;
	}
	
	
	public int getDRAW_TYPE() {
		return DRAW_TYPE;
	}
	public Model setDRAW_TYPE(int dRAW_TYPE) {
		DRAW_TYPE = dRAW_TYPE;
		return this;
	}
	public int getFILL_MODE() {
		return FILL_MODE;
	}
	public Model setFILL_MODE(int fILL_MODE) {
		FILL_MODE = fILL_MODE;
		return this;
	}
	public AABB getBounds() {
		return bounds;
	}
	public Model setBounds(AABB bounds) {
		this.bounds = bounds;
		return this;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Sets the offset
	 * @param offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@Override
	public String toString() {
		return "Type:"+DRAW_TYPE+",Elemets:"+ElementDrawCount+",Bounds:"+bounds.toString();
	}
	
	public static void resetStats() {
		Model.DrawCalls = 0;
		Model.DrawElementCalls = 0;
		InstancedModel.InstancedModelDrawCalls = 0;
		InstancedModel.InstancedModelDrawings = 0;
	}
	
	public int getVertex_count() {
		return vertex_count;
	}
	
}
