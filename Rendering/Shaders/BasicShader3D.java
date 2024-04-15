package Kartoffel.Licht.Rendering.Shaders;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import Kartoffel.Licht.Java.ModifiableVariable;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Animation.Animation;
import Kartoffel.Licht.Rendering.Animation.SkeletalAnimation;
import Kartoffel.Licht.Rendering.Shaders.Objects.Light;
import Kartoffel.Licht.Rendering.Texture.CubeMap;
import Kartoffel.Licht.Rendering.Texture.MultiTexture;
import Kartoffel.Licht.Res.FileLoader;




//Object that can render.
public class BasicShader3D extends Shader{
	
	public static float AMBIENT_MULTIPL = 0.1f;
	
	public static final float[] Attenuation_NULL = new float[] {0, 0};
	public static final float[] Attenuation_7 = new float[] {0.7f, 1.8f};
	public static final float[] Attenuation_13 = new float[] {0.35f, 0.44f};
	public static final float[] Attenuation_20 = new float[] {0.22f, 0.20f};
	public static final float[] Attenuation_32 = new float[] {0.14f, 0.07f};
	public static final float[] Attenuation_50 = new float[] {0.09f, 0.032f};
	public static final float[] Attenuation_65 = new float[] {0.07f, 0.017f};
	public static final float[] Attenuation_100 = new float[] {0.045f, 0.0075f};
	public static final float[] Attenuation_160 = new float[] {0.027f, 0.0028f};
	public static final float[] Attenuation_200 = new float[] {0.022f, 0.0019f};
	public static final float[] Attenuation_325 = new float[] {0.014f, 0.0007f};
	public static final float[] Attenuation_600 = new float[] {0.007f, 0.0002f};
	public static final float[] Attenuation_3250 = new float[] {0.0014f, 0.000007f};
	public static final String Attenuation_Source = "https://wiki.ogre3d.org/tiki-index.php?page=-Point+Light+Attenuation";
	
	@ModifiableVariable
	private Vector3f GlobalLightDirection = new Vector3f(0, -1, 0);
	@ModifiableVariable
	private Vector3f GlobalLightColor = new Vector3f(1.6f, 1.6f, 1.6f);
	@ModifiableVariable
	private List<Light> Lights = new ArrayList<Light>();
	
	private Camera camera;
	
	private CubeMap skybox;
	
	private boolean shading = true;
	private boolean normalMapping = true;
	private boolean noNormals = false;
	private boolean skyboxReflection = false;

	public BasicShader3D(Camera cam) {
		super(FileLoader.readFileD("default/shaders/basic.vert"), FileLoader.readFileD("default/shaders/basic.frag"), FileLoader.readFileD("default/shaders/basic.geo"), "Basic 3D Shader");
		camera = cam;
	}
	
	@Override
	final protected void setUniforms(GEntity entity) {
		//Default uniforms
		this.bind();
		this.setUniformInt("mate.albedo", 0);
		this.setUniformInt("mate.metallic", 1);
		this.setUniformInt("mate.roughness", 2);
		this.setUniformInt("mate.emissive", 3);
		this.setUniformInt("mate.normal", 4);
		this.setUniformInt("mate.ao", 5);
		this.setUniformInt("skybox", 6);
		this.setUniformBool("shading", shading);
		this.setUniformBool("noNormal", noNormals);
		this.setUniformBool("sky_reflection", skyboxReflection);
		this.bindNullShader();
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
		if(skybox != null)
			skybox.bind(6);
		if(gentity.getMod() != null)
			this.setUniformInt("materialCount", gentity.getMod().getMaterial_count());
		else
			this.setUniformInt("materialCount", 1);
		//Directonal Light
		if(shading) {
			this.setUniformVec3("DirectionalLight.direction", GlobalLightDirection);
			this.setUniformVec3("DirectionalLight.color", GlobalLightColor);
			this.setUniformVec3("DirectionalLight.ambient", GlobalLightColor.x*AMBIENT_MULTIPL, GlobalLightColor.y*AMBIENT_MULTIPL, GlobalLightColor.z*AMBIENT_MULTIPL);
			//Lights
			int asl = 0;
			int apl = 0;
			for(int i = 0;i < Lights.size(); i++) {
				boolean spot = Lights.get(i).spotlight;
				Lights.get(i).addLight(this, spot ? asl : apl);
				if(spot)
					asl++;
				else
					apl++;
			}
			this.setUniformInt("plAm", apl);
			this.setUniformInt("slAm", asl);
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
			if(gentity.getTex() instanceof MultiTexture) {
				normalMapping = ((MultiTexture)gentity.getTex()).getAmount() > 4; //Enable normal mapping if normal map texture is available
			}
		}
		this.setUniformBool("normalMapping", normalMapping); //Update uniform
		if(gentity.getMod() != null)
			gentity.getMod().render();
		this.bindNullShader();
	}
	
	
	public Light addLight(Vector3f position, Vector3f color) {
		Light l = new Light(position, color);
		Lights.add(l);
		return l;
				
	}
	
	public Light addSpotLight(Vector3f position, Vector3f color, Vector3f direction, float cutoff, float edge) {
		Light l = new Light(position, color, direction, edge, cutoff);
		Lights.add(l);
		return l;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	
	public void removeLight(int l) {
		Lights.remove(l);
	}
	public void clearLights() {
		Lights.clear();
	}


	public Vector3f getGlobalLightDirection() {
		return GlobalLightDirection;
	}


	public void setGlobalLightDirection(Vector3f globalLightDirection) {
		GlobalLightDirection = globalLightDirection;
	}
	public void setGlobalLightDirection(float a, float b, float c) {
		GlobalLightDirection.x = a;
		GlobalLightDirection.y = b;
		GlobalLightDirection.z = c;
	}


	public Vector3f getGlobalLightColor() {
		return GlobalLightColor;
	}


	public void setGlobalLightColor(Vector3f globalLightColor) {
		GlobalLightColor = globalLightColor;
	}
	public void setGlobalLightColor(float a, float b, float c) {
		GlobalLightColor.x = a;
		GlobalLightColor.y = b;
		GlobalLightColor.z = c;
	}
	
	public void setSkybox(CubeMap skybox) {
		this.skybox = skybox;
	}
	
	public void setLights(List<Light> lights) {
		Lights = lights;
	}
	public List<Light> getLights() {
		return Lights;
	}
	
	public void setShading(boolean shading) {
		this.shading = shading;
	}
	public boolean isShading() {
		return shading;
	}
	public boolean isNormalMapping() {
		return normalMapping;
	}
	public void setNormalMapping(boolean normalMapping) {
		this.normalMapping = normalMapping;
	}
	public void setNoNormals(boolean b) {
		noNormals = b;
	}
	public boolean isNoNormals() {
		return noNormals;
	}
	public void setSkyboxReflections(boolean b) {
		skyboxReflection = b;
	}
	public boolean isSkyboxReflection() {
		return skyboxReflection;
	}
	public Camera getCamera() {
		return camera;
	}

}