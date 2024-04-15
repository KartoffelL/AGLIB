package Kartoffel.Licht.Rendering.Shaders;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Animation.Animation;
import Kartoffel.Licht.Rendering.Animation.SkeletalAnimation;
import Kartoffel.Licht.Res.FileLoader;




//Object that can render.
public class SimpleShader extends Shader{
	
	private Camera camera;

	public SimpleShader(Camera cam, String fragment, String geometry, String name) {
		super(FileLoader.readFileD("default/shaders/simple.vert"), fragment, geometry, name);
		camera = cam;
	}
	
	
	@Override
	public void draw(GEntity gentity) {
		if(gentity == null)
			return;
		this.bind();
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat",camera.getViewMatrix());
		this.setUniformMatrix4f("viewMatInv",camera.getViewMatrixInv());
		this.setUniformMatrix4f("transformationMat",gentity.getTransformationMatrix());
		this.setUniformMatrix4f("transformationMatInv",gentity.getTransformationMatrixInv());
		
		if(gentity.getTex() != null) {
			gentity.getTex().bind(0);
			if(gentity.getTex().getFlags() != null)
				if(gentity.getTex().getFlags().containsKey("repetetion"))
					this.setUniformInt("tex_repetetion", gentity.getTex().getFlags().get("repetetion"));
		}
		//Animation
		Animation anim = gentity.getAnimation();
		if(anim != null) {
			anim.getTransformations(this);
		}
		else
			SkeletalAnimation.getDefaultTransformations(this);
		if(gentity.getTex() != null) {
			gentity.getTex().bind(0);
		}
		if(gentity.getMod() != null)
			gentity.getMod().render();
		this.bindNullShader();
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public Camera getCamera() {
		return camera;
	}

}