package Kartoffel.Licht.Rendering.Shaders;

import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Res.FileLoader;

//Object that can render.
public class BasicShader2D extends Shader{
	
	public BasicShader2D() {
		super(FileLoader.readFileD("default/shaders/basic2D.vert"), FileLoader.readFileD("default/shaders/basic2D.frag"), "Basic 2D Shader");
	}
	
	public BasicShader2D(boolean IsStaticOrNot) {
		super(FileLoader.readFileD("default/shaders/sbasic2D.vert"), FileLoader.readFileD("default/shaders/basic2D.frag"), "Static Basic 2D Shader");
	}
	
	@Override
	protected void setUniforms(GEntity entity) {
		bind();
		if(entity != null)
			this.setUniformMatrix4f("transformationMat", entity.getTransformationMatrix());
	}
	
}

