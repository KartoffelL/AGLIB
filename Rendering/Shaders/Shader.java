package Kartoffel.Licht.Rendering.Shaders;

import static Kartoffel.Licht.Tools.Tools.SRL;
import static Kartoffel.Licht.Tools.Tools.err;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Java.opengl;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.InstancedModel;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Texture.Renderable;
/**
 * Shaders are programs that can be uploaded to the GPU.<br>
 *
 */
@opengl
public class Shader implements namable, freeable{
	
	private static FloatBuffer r_buffer = BufferUtils.createFloatBuffer(16);
	
	private String name = "Shader";
	
	
	protected int program;
	protected int vertexShader;
	protected int fragmentShader;
	protected int geometryshader;
	protected boolean valid = false;
	
	protected String vertexFile;
	protected String fragmentFile;
	protected String geometryFile;
	
	protected String[] ERRORS = new String[3];
	protected String[][] uniforms;

	/**
	 * Creates a new Shader.
	 * @param vertexShaderFile the Vertex-Shader Program in glsl
	 * @param fragmentShaderFile the Fragment-Shader Program in glsl
	 * @param name
	 */
	public Shader(String vertexShaderFile, String fragmentShaderFile, String name) {
		main(vertexShaderFile, fragmentShaderFile, null, name);
	}
	/**
	 * Creates a new Shader.
	 * @param vertexShaderFile the Vertex-Shader Program in glsl
	 * @param fragmentShaderFile the Fragment-Shader Program in glsl
	 * @param geometryShaderFile the Geometry-Shader Program in glsl
	 * @param name
	 */
	public Shader(String vertexShaderFile, String fragmentShaderFile, String geometryShaderFile, String name) {
		main(vertexShaderFile, fragmentShaderFile, geometryShaderFile, name);
	}
	
	private void main(String vertexShaderFile, String fragmentShaderFile, String geometryShaderFile, String name) {
		this.name = name;
		this.vertexFile = vertexShaderFile;
		this.fragmentFile = fragmentShaderFile;
		this.geometryFile = geometryShaderFile;
		uploadProgram(vertexShaderFile, fragmentShaderFile, geometryShaderFile);
	}
	
	
	/**
	 * Updates the shaders of the Program. Uniform variables have to be set again
	 */
	public final void updateProgram() {
		uploadProgram(vertexFile, fragmentFile, geometryFile);
	}
	/**
	 * Sets the shader to the given Strings. May be null
	 * @param vs the Vertex-shader
	 * @param fs the Fragment-shader
	 * @param gs the Geometry-shader
	 */
	public final void setShader(String vs, String fs, String gs) {
		if(fs != null)
			this.fragmentFile = fs;
		if(vs != null)
			this.vertexFile = vs;
		if(gs != null)
			this.geometryFile = gs;
		updateProgram();
	}
	private final void analyzeUniforms(String vs, String fs, String gs) {
		uniforms = new String[3][];
		if(vs != null) {
			List<String> un = new ArrayList<>();
			int index = 0;
			while(index < vs.length()) {
				index = vs.indexOf("uniform", index);
				int li = vs.indexOf(";", index);
				if(index < li && index != -1) {
					un.add(vs.substring(index, li));
				}
				else
					break;
				index++;
			}
			uniforms[0] = un.toArray(new String[un.size()]);
		}
		if(fs != null) {
			List<String> un = new ArrayList<>();
			int index = 0;
			while(index < fs.length()) {
				index = fs.indexOf("uniform", index);
				int li = fs.indexOf(";", index);
				if(index < li && index != -1) {
					un.add(fs.substring(index, li));
				}
				else
					break;
				index++;
			}
			uniforms[1] = un.toArray(new String[un.size()]);
		}
		if(gs != null) {
			List<String> un = new ArrayList<>();
			int index = 0;
			while(index < gs.length()) {
				index = gs.indexOf("uniform", index);
				int li = gs.indexOf(";", index);
				if(index < li && index != -1) {
					un.add(gs.substring(index, li));
				}
				else
					break;
				index++;
			}
			uniforms[2] = un.toArray(new String[un.size()]);
		}
	}
	/**
	 * Creates shaders, uploads, compiles them, analyses uniforms, etc...
	 * @param vertexShaderFile may be null
	 * @param fragmentShaderFile may be null
	 * @param geometryShaderFile may be null
	 */
	public final void uploadProgram(String vertexShaderFile, String fragmentShaderFile, String geometryShaderFile) {
		analyzeUniforms(vertexShaderFile, fragmentShaderFile, geometryShaderFile);
		//free old shader
		if(program != 0)
			this.free();
		//upload new one
		program = glCreateProgram();
		
		if(vertexShaderFile != null) {
			vertexShader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertexShader, vertexShaderFile);
			//Compiling
			glCompileShader(vertexShader);
			if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) != 1) {
				SRL("Problem with V Shader: " + this.name);
				SRL(glGetShaderInfoLog(vertexShader));
				ERRORS[0] = glGetShaderInfoLog(vertexShader);
				return;
			}else
				ERRORS[0] = "";
			glAttachShader(program, vertexShader);
		}
		
		if(fragmentShaderFile != null) {
			fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragmentShader, fragmentShaderFile);
			//Compiling
			glCompileShader(fragmentShader);
			if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != 1) {
				SRL("Problem with F Shader: " + this.name);
				SRL(glGetShaderInfoLog(fragmentShader));
				ERRORS[1] = glGetShaderInfoLog(fragmentShader);
				return;
			}else
				ERRORS[1] = "";
			glAttachShader(program, fragmentShader);
		}
		
		if(geometryShaderFile != null) {
			geometryshader = glCreateShader(GL_GEOMETRY_SHADER);
			glShaderSource(geometryshader, geometryShaderFile);
			//Compiling
			glCompileShader(geometryshader);
			if(glGetShaderi(geometryshader, GL_COMPILE_STATUS) != 1) {
				SRL("Problem with G Shader: " + this.name);
				SRL(glGetShaderInfoLog(geometryshader));
				ERRORS[2] = glGetShaderInfoLog(geometryshader);
				return;
			}else
				ERRORS[2] = "";
			glAttachShader(program, geometryshader);
		}
		
		//Attributes
		glBindAttribLocation(program, 0, "vertices");
		glBindAttribLocation(program, 1, "textures");
		glBindAttribLocation(program, 2, "normals");
		
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) != 1) {
			System.out.println();
			err("Error while linking program: " + this.name);
			err(GL33.glGetProgramInfoLog(program));
			return;
		}
		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
			err("Error while validating program: " + this.name);
			err(GL33.glGetProgramInfoLog(program));
			return;
		}
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
		glDeleteShader(geometryshader);
		//GL30.glBindVertexArray(0);
		valid = true;
	
	}
	/**
	 * Draws a GEntity using this Shader
	 * @param gentity
	 */
	final public void render(GEntity gentity) {
		setUniforms(gentity);
		draw(gentity);
	}
	/**
	 * Draws all GEntities in the given List using this Shader
	 * @param gentity
	 */
	final public void render(List<GEntity> gentity) {
		for(GEntity g : gentity) {
			setUniforms(g);
			draw(g);
		}
	}
	protected void draw(GEntity gentity) {
		this.bind();
		if(gentity.getTex() != null)
			gentity.getTex().bind(0);
		Model mod = gentity.getMod();
		if(mod != null)
			if(mod instanceof InstancedModel) {
				((InstancedModel)mod).render();
			}else {
				mod.render();
			}
		this.bindNullShader();
	}
	
	/**
	 * Draws the Model with the texture using this Shader
	 * @param mod
	 * @param tex
	 */
	final public void render(Model mod, Renderable tex) {
		setUniforms(null);
		draw(mod, tex);
	}
	
	protected void draw(Model mod, Renderable tex) {
		this.bind();
		if(tex != null)
			tex.bind(0);
		if(mod != null)
			if(mod instanceof InstancedModel) {
				((InstancedModel)mod).render();
			}else {
				mod.render();
			}
		this.bindNullShader();
	}
	@Override
	public void free() {
		glDeleteProgram(program);
	}
	
	//#######################
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformInt(String name, int value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform1i(location, value);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformFloat(String name, float value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform1f(location, value);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformBool(String name, boolean value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform1i(location, value ? 1 : 0);
		else return false;
		return true;
	}
	
	//#######################
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec2i(String name, int x, int y) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2f(location, x, y);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec2i(String name, Vector2i value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2f(location, value.x, value.y);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec2(String name, float x, float y) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2f(location, x, y);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec2(String name, Vector2f value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2f(location, value.x, value.y);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec2b(String name, boolean x, boolean y) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform2i(location, x ? 1 : 0, location);
		else return false;
		return true;
	}
	
	//#######################
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec3i(String name, Vector3i value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform3i(location, value.x, value.y, value.z);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec3i(String name, int a, int b, int c) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform3i(location, a, b, c);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec3(String name, Vector3f value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform3f(location, value.x, value.y, value.z);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec3(String name, float a, float b, float c) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform3f(location, a, b, c);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec3b(String name, boolean a, boolean b, boolean c) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			glUniform3i(location, a ? 1 : 0, b ? 1 : 0, c ? 1 : 0);
		else return false;
		return true;
	}
	
	//#######################
	
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec4i(String name, Vector4i value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			GL33.glUniform4i(location, value.x, value.y, value.z, value.w);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec4i(String name, int x, int y, int z, int w) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			GL33.glUniform4i(location, x, y, z, w);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec4(String name, Vector4f value) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			GL33.glUniform4f(location, value.x, value.y, value.z, value.w);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec4(String name, float x, float y, float z, float w) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			GL33.glUniform4f(location, x, y, z, w);
		else return false;
		return true;
	}
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformVec4b(String name, boolean x, boolean y, boolean z, boolean w) {
		int location = glGetUniformLocation(program, name);
		if(location != -1)
			GL33.glUniform4i(location, x ? 1 : 0, y ? 1 : 0, z ? 1 : 0, w ? 1 : 0);
		else return false;
		return true;
	}
	
	/**
	 * Sets the value of an uniform variable.<br>
	 * Uniform variables are shared between vertex, geometry and fragment shaders.<br>
	 * If modifying an uniform variable, the right method should be used, eg:<r>
	 * <br>
	 * <br>
	 * Shader Code:<br>
	 * <code>
	 * ...<br>
	 * uniform float speed;<br>
	 * uniform bool hasAlpha = false;<br>
	 * uniform vec3 up = vec3(0, 1, 0);<br>
	 * ...<br>
	 * </code>
	 * Java Code:<br>
	 * <code>
	 * ...<br>
	 * shader.setUniformBool("hasAlpha", true); //Sets 'hasAlpha' to true!<br><br>
	 * shader.setUniformInt("speed", 1) //Failed, because 'speed' is an float, but an Integer Method was used!<br><br>
	 * shader.setUniformVec3("UP", 1, 0, 0); //Failed, because variable name isin´t exact. Should be the same case in shader and java!<br><br>
	 * ...<br>
	 * </code>
	 * @param the name exact name of the uniform variable
	 * @param value the value
	 * @return if the uniform variable was found
	 */
	public final boolean setUniformMatrix4f(String name, Matrix4f value) {
		int location = glGetUniformLocation(program, name);
		value.get(r_buffer);
		if(location != -1)
			glUniformMatrix4fv(location, false, r_buffer);
		else return false;
		return true;
	}
	/**
	 * WIP
	 * @param variable
	 * @param binding
	 */
	public final void setUniformBlockBinding(String variable, int binding) {
		int loc = GL33.glGetUniformBlockIndex(program, variable);
		GL33.glUniformBlockBinding(program, loc, binding);
	}
	
	protected void setUniforms(GEntity entity) {
		//Uniforms to be set
	}
	/**
	 * Binds the Shader
	 */
	public final void bind() {
		glUseProgram(program);
	}
	/**
	 * Binds default Shader
	 */
	public final void bindNullShader() {
		glUseProgram(0);
	}
	/**
	 * If the Shader is valid
	 * @return
	 */
	public final boolean isValid() {
		return valid;
	}
	/**
	 * Returns the openGL program ID
	 * @return
	 */
	public int getProgram() {
		return program;
	}
	/**
	 * Sets the openGL program ID
	 * @param program
	 */
	public void setProgram(int program) {
		this.program = program;
	}
	/**
	 * Returns the Vertex openGL shader ID
	 * @return
	 */
	public int getVertexShader() {
		return vertexShader;
	}
	/**
	 * Sets the vertex openGL shader ID
	 * @param vertexShader
	 */
	public void setVertexShader(int vertexShader) {
		this.vertexShader = vertexShader;
	}
	/**
	 * Returns the fragment openGL shader ID
	 * @return
	 */
	public int getFragmentShader() {
		return fragmentShader;
	}
	/**
	 * Sets the fragment openGL shader ID
	 * @param fragmentShader
	 */
	public void setFragmentShader(int fragmentShader) {
		this.fragmentShader = fragmentShader;
	}
	/**
	 * Returns the geometry openGL shader ID
	 * @return
	 */
	public int getGeometryshader() {
		return geometryshader;
	}
	/**
	 * Sets the geometry openGL shader ID
	 * @param geometryshader
	 */
	public void setGeometryshader(int geometryshader) {
		this.geometryshader = geometryshader;
	}
	/**
	 * Returns the vertex shader source code
	 * @return
	 */
	public String getVertexFile() {
		return vertexFile;
	}
	/**
	 * Sets the vertex shader source code. Use 'updateProgram()' to update the Program
	 * @param vertexFile
	 */
	public void setVertexFile(String vertexFile) {
		this.vertexFile = vertexFile;
	}
	/**
	 * Returns the fragment shader source code
	 * @return
	 */
	public String getFragmentFile() {
		return fragmentFile;
	}
	/**
	 * Sets the fragment shader source code. Use 'updateProgram()' to update the Program
	 * @param fragmentFile
	 */
	public void setFragmentFile(String fragmentFile) {
		this.fragmentFile = fragmentFile;
	}
	/**
	 * Returns the geometry shader source code
	 * @return
	 */
	public String getGeometryFile() {
		return geometryFile;
	}
	/**
	 * Sets the geometry shader source code. Use 'updateProgram()' to update the Program
	 * @param geometryFile
	 */
	public void setGeometryFile(String geometryFile) {
		this.geometryFile = geometryFile;
	}
	/**
	 * Returns any errors
	 * @return String[] {VertexShaderErrors, FragmentShaderErrors, GeometryShaderErrors}
	 */
	public String[] getErrors() {
		return ERRORS;
	}
	/**
	 * Returns the uniforms detected by the Shader
	 * @return String[][] {VertexShaderUniforms:{...}, FragmentShaderUniforms:{...}, GeometryShaderUniforms:{...}}
	 */
	public String[][] getUniforms() {
		return uniforms;
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
