package Kartoffel.Licht.Rendering.Shaders;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.InstancedModel;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.VoxelGrid;
import Kartoffel.Licht.Res.FileLoader;

public class VoxelisationShader extends Shader{

	private Camera cam;
	private VoxelGrid color;
	private VoxelGrid normal;
	private VoxelGrid material;
	
	public VoxelisationShader(Camera cam, VoxelGrid color, VoxelGrid normal, VoxelGrid material) {
		super(FileLoader.readFileD("default/shaders/voxel.vert"), FileLoader.readFileD("default/shaders/voxel.frag"), "Basic 2D Shader");
		this.cam = cam;
		this.color = color;
		this.normal = normal;
		this.material = material;
	}
	@Override
	final protected void setUniforms(GEntity entity) {
		//Default uniforms
		this.bind();
		this.setUniformMatrix4f("transformationMat", entity.getTransformationMatrix());
		this.setUniformMatrix4f("viewMat", cam.getViewMatrix());
		this.setUniformMatrix4f("projectionMat", cam.getProjection());
		this.setUniformInt("texture1", 0);
		this.setUniformInt("texture2", 1);
		this.setUniformInt("texture3", 2);
		this.setUniformInt("sampler0", 3);
		color.bind(0);
		normal.bind(1);
		material.bind(2);
		this.bindNullShader();
	}
	@Override
	protected void draw(Model mod, Renderable tex) {
		this.bind();
		tex.bind(3);
		if(mod != null)
			if(mod instanceof InstancedModel) {
				((InstancedModel)mod).render();
			}else {
				mod.render();
			}
		this.bindNullShader();
	}
	@Override
	protected void draw(GEntity gentity) {
		this.bind();
		if(gentity.getTex() != null)
			gentity.getTex().bind(3);
		Model mod = gentity.getMod();
		if(mod != null)
			if(mod instanceof InstancedModel) {
				((InstancedModel)mod).render();
			}else {
				mod.render();
			}
		this.bindNullShader();
	}


	public void setCam(Camera cam) {
		this.cam = cam;
	}
	public Camera getCam() {
		return cam;
	}
	public void setVoxelGrid(VoxelGrid vg) {
		this.color = vg;
	}
	public VoxelGrid getVoxelGrid() {
		return color;
	}
	public VoxelGrid getNormal() {
		return normal;
	}
	public void setNormal(VoxelGrid normal) {
		this.normal = normal;
	}
	public VoxelGrid getMaterial() {
		return material;
	}
	public void setMaterial(VoxelGrid material) {
		this.material = material;
	}
}
