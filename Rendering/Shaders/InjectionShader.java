package Kartoffel.Licht.Rendering.Shaders;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Timer;

public class InjectionShader extends SimpleShader{
	
	public InjectionShader(Camera cam) {
		super(cam, FileLoader.readFileD("default/shaders/inject.frag"), null, "Injection Shader");
	}
	
	@Override
	protected void setUniforms(GEntity entity) {
		//Default uniforms
		this.bind();
		this.setUniformInt("sampler0", 0);
		this.setUniformInt("sampler1", 1);
		this.setUniformInt("sampler2", 2);
		this.setUniformInt("sampler3", 3);
		this.setUniformInt("sampler4", 4);
		this.setUniformInt("sampler5", 5);
		this.setUniformInt("sampler6", 6);
		this.setUniformInt("sampler7", 7);
		this.setUniformFloat("time", Timer.getTime()/1000000000.0f);
		this.bindNullShader();
	}

}