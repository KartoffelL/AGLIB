package Kartoffel.Licht.Rendering.Shaders;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Res.FileLoader;




//Object that can render.
public class ShadowShader extends Shader{
	
	private Camera camera;

	public ShadowShader(Camera cam) {
		super(FileLoader.readFileD("default/shaders/simple.vert"), null, null, "Basic 3D Shader");
		camera = cam;
	}
	
	@Override
	final protected void setUniforms(GEntity entity) {
		//Default uniforms
		this.bind();
		this.setUniformInt("material.albedo", 0);
		this.setUniformInt("material.metallic", 1);
		this.setUniformInt("material.roughness", 2);
		this.setUniformInt("material.emissive", 3);
		this.setUniformInt("skybox", 4);
		this.bindNullShader();
	}
	
	@Override
	public void draw(GEntity gentity) {
		this.bind();
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat",camera.getViewMatrix());
		this.setUniformMatrix4f("viewMatInv",camera.getViewMatrixInv());
		this.setUniformMatrix4f("transformationMat",gentity.getTransformationMatrix());
		this.setUniformMatrix4f("transformationMatInv",gentity.getTransformationMatrixInv());
		
		
		if(gentity.getTex() != null)
			gentity.getTex().bind(0);
		if(gentity.getMod() != null)
			gentity.getMod().render();
		this.bindNullShader();
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

}