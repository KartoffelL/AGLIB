package Kartoffel.Licht.Rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.Geo.AABB;

public class CompositeModel extends Model{

	private List<Model> models = new ArrayList<Model>();
	
	public CompositeModel(Model...models) {
		for(Model m : models)
			this.models.add(m);
		this.material_count = 1;
		this.DRAW_TYPE = GL_TRIANGLES;
		this.FILL_MODE = GL_FILLMODE_FILL;
	}
	public List<Model> getModels() {
		return models;
	}
	
	@Override
	public void free() {
		for(Model m : models)
			m.free();
	}
	
	@Override
	public void render() {
		for(Model m : models)
			m.render();
	}
	@Override
	public void setOffset(int offset) {
		for(Model m : models)
			m.setOffset(offset);
	}
	
	@Override
	public int getVertex_id() {
		throw new RuntimeException("Can not return value of composite model property!");
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new CompositeModel((Model[]) models.toArray(new Model[models.size()]));
	}
	
	@Override
	protected FloatBuffer createFloatBuffer(float[] data) {
		return super.createFloatBuffer(data);
	}
	
	@Override
	protected IntBuffer createIntBuffer(int[] data) {
		return super.createIntBuffer(data);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int get(String s) {
		throw new RuntimeException("Composite model has no buffers!");
	}
	
	@Override
	public AABB getBounds() {
		throw new RuntimeException("Composite model has no bounds!");
	}
	
	@Override
	public int getDRAW_TYPE() {
		return DRAW_TYPE;
	}
	
	@Override
	public int getElementDrawCount() {
		throw new RuntimeException("Composite model has no draw count!");
	}
	
	@Override
	public int getFILL_MODE() {
		return FILL_MODE;
	}
	@Override
	public int getIndices_id() {
		throw new RuntimeException("Composite model has no index buffer!");
	}
	@Override
	public int getMaterial_count() {
		return material_count;
	}
	public Model setMaterial_count(int count) {
		this.material_count = count;
		return this;
	}
	@Override
	public int getMaterial_id() {
		throw new RuntimeException("Composite model has no material buffer!");
	}@Override
	public String getName() {
		return super.getName();
	}
	@Override
	public int getNormal_id() {
		throw new RuntimeException("Composite model has no normal buffer!");
	}@Override
	public int getOffset() {
		throw new RuntimeException("Composite model has no offset!");
	}@Override
	public int getTexture_id() {
		throw new RuntimeException("Composite model has no texture buffer!");
	}@Override
	public int getVertex_count() {
		throw new RuntimeException("Composite model has no vertices!");
	}@Override
	public int hashCode() {
		return super.hashCode();
	}@Override
	public boolean isFace_Culling_enabled() {
		return Face_Culling_enabled;
	}@Override
	public Model setBounds(AABB bounds) {
		throw new RuntimeException("Composite model can not have any bounds set!");
	}@Override
	public Model setDRAW_TYPE(int dRAW_TYPE) {
		for(Model m : models)
			m.setDRAW_TYPE(dRAW_TYPE);
		this.DRAW_TYPE = dRAW_TYPE;
		return this;
	}@Override
	public Model setFaceCulling(boolean face_Culling_enabled) {
		for(Model m : models)
			m.setFaceCulling(face_Culling_enabled);
		this.Face_Culling_enabled = face_Culling_enabled;
		return this;
	}@Override
	public Model setFILL_MODE(int fILL_MODE) {
		for(Model m : models)
			m.setFILL_MODE(fILL_MODE);
		this.FILL_MODE = fILL_MODE;
		return this;
	}@Override
	public void setName(String name) {
		super.setName(name);
	}@Override
	public String toString() {
		return "Composite Model";
	}

}
