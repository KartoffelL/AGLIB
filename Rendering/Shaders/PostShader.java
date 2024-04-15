package Kartoffel.Licht.Rendering.Shaders;


import org.joml.Vector2f;

import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shapes.SPBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Res.FileLoader;

//Object that can render.
public class PostShader extends Shader{
	
	private static Model plane;
	public static final String DEFAULT_VERTEX_SHADER = FileLoader.readFileD("default/shaders/post/basic.vert");
	public static final String[] POST_SHADER_BLURR = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/blurr.frag")};
	public static final String[] POST_SHADER_DOWNSCALE = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/downscale.frag")};
	public static final String[] POST_SHADER_PASS = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/pass.frag")};
	public static final String[] POST_SHADER_BLOOM = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/bloom.frag")};
	public static final String[] POST_SHADER_BASIC = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/basic.frag") };
	public static final String[] POST_SHADER_INVERT = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/invert.frag") };
	public static final String[] POST_SHADER_MIX = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/mix.frag") };
	public static final String[] POST_SHADER_MUL = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/mul.frag") };
	public static final String[] POST_SHADER_AO = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/ao.frag") };
	public static final String[] POST_SHADER_DNOISE = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/noise.frag") };
	public static final String[] POST_SHADER_FXAA = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/fxaa.frag") };
	public static final String[] POST_SHADER_FOG = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/fog.frag") };
	public static final String[] POST_SHADER_DOF = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/dof.frag")};
	public static final String[] POST_SHADER_VOXEL = new String[] {DEFAULT_VERTEX_SHADER, FileLoader.readFileD("default/shaders/post/voxel.frag")};
	
	public PostShader(String v, String f) {
		super(v, f, "Post Processing Shader");
		if(plane == null)
			plane = new Model(new SPBox2D(1f, 1f));
	}
	
	public PostShader(String[] sh) {
		super(sh[0], sh[1], "Post Processing Shader");
		if(plane == null)
		plane = new Model(new SPBox2D(1f, 1f));
	}
	
	
	@Override
	public void draw(GEntity gentity) {
		//This Shader cant render Normal Entities
		throw new IllegalAccessError("This shader can�t render entities normal. use render(Renderable...textureID)");
	}
	@Override
	protected void draw(Model mod, Renderable tex) {
		//This Shader cant render Normal Entities
				throw new IllegalAccessError("This shader can�t render entities normal. use render(Renderable...textureID)");
	}
	
	public final void render(Renderable...textureID) {
		this.bind();
		this.setUniforms(null);
		if(textureID.length < 1)
			return;
		int textureSize = Math.min(8, textureID.length);
		
		for(int i = 0; i < textureSize; i++) {
			if(textureID[i] != null) {
				textureID[i].bind(i);
			}
			this.setUniformInt("sampler"+i, i);
		}
			
		plane.render();
		
		this.bindNullShader();
	}
	
	@Override
	protected void setUniforms(GEntity entity) {
		this.setUniformVec2("screenSize", new Vector2f(1000, 1000));
	}
	public static Model getPlane() {
		return plane;
	}
	
}
