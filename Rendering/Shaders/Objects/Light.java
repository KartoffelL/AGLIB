package Kartoffel.Licht.Rendering.Shaders.Objects;

import org.joml.Vector3f;

import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Shaders.Shader;

public class Light implements namable{
	
	private String name;
	
	public boolean spotlight;
	
	public Vector3f position;
	public Vector3f color;
	public Vector3f direction;
	public float cutoff = .5f;
	public float edge = .5f;
	public Light(Vector3f position, Vector3f color) {
		super();
		this.position = position;
		this.color = color;
		this.direction = new Vector3f();
		spotlight = false;
	}
	
	public Light(Vector3f position, Vector3f color, Vector3f direction,float edge, float cutoff) {
		super();
		this.position = position;
		this.color = color;
		this.direction = direction;
		this.cutoff = cutoff;
		this.edge = edge;
		spotlight = true;
	}
	
	public void addLight(Shader sh, int index) {
		if(spotlight) {
			sh.setUniformVec3("SpotLights["+index+"].position", position);
			sh.setUniformVec3("SpotLights["+index+"].color", color);
			sh.setUniformVec3("SpotLights["+index+"].direction", direction);
			sh.setUniformFloat("SpotLights["+index+"].cutoff", cutoff);
			sh.setUniformFloat("SpotLights["+index+"].edge", edge);
		}
		else {
			sh.setUniformVec3("PointedLights["+index+"].position", position);
			sh.setUniformVec3("PointedLights["+index+"].color", color);
		}
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
