package Kartoffel.Licht.Rendering.Texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.HashMap;

import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Texture.Material.Material;
import Kartoffel.Licht.Rendering.Texture.Material.MaterialType;
import Kartoffel.Licht.Rendering.Texture.Material.PBRMaterial;
import Kartoffel.Licht.Tools.MaterialLoader;
/**
 * A Texture is an Object, that stores buffers on the GPU.
 * Its like a Ticket to acces the uploaded Data.
 * Can only be created in the Rendering Thread.
 * 
 * 
 */
public class TextureMaterial implements MultiTexture{

	private TextureArray textures;
	
	private MaterialType[] material;
	
	private String name = "Texture Material";
	
	//Flags
	private HashMap<String, Integer> flags = new HashMap<String, Integer>();
	public int repetetion = 0;
	
	//###########################CONSTRUCTORS##############################
	
	//Default
	public TextureMaterial() {
		this.material = new PBRMaterial[1];
		this.material[0] = PBRMaterial.MATERIAL_DEFAULT;
		CMTT();
	}
	//One Material
	public TextureMaterial(Material material) {
		this.material = new Material[1];
		this.material[0] = material;
		CMTT();
	}
	//Many materials
	public TextureMaterial(Material... material) {
		this.material = material;
		CMTT();
	}
	//MTL loader
	public TextureMaterial( MaterialLoader l) {
		this.material = new Material[l.getMaterials().size()];
		this.material = l.getMaterials().toArray(this.material);
		CMTT();
	}
	//Material maps
	public TextureMaterial(String diff, String amb, String spec, String shin) {
		this.textures = new TextureArray(diff, amb, spec, shin);
	}
	public TextureMaterial(BufferedImage a, BufferedImage b, BufferedImage c, BufferedImage d) {
		this.textures = new TextureArray(a, b, c, d);
	}
	public TextureMaterial(Color diff, Color amb, Color spec, Color shin) {
		this.textures = new TextureArray(diff, amb, spec, shin);
	}
	
	//PBR Material
	public TextureMaterial(PBRMaterial material) {
		this.material = new MaterialType[1];
		this.material[0] = material;
		CPBRMTT();
	}
	
	//#####################COPY####################################
	
	
	//#####################INTERN####################################
	/**Convert materials to texture**/
	public void CMTT() {
		int materialCount = material.length;
		if(materialCount > 0)
			if(material[0] instanceof PBRMaterial) {
				CPBRMTT();
				return;
			}
		BufferedImage df = new BufferedImage(materialCount, 1, 3);  //diffuse
		BufferedImage am = new BufferedImage(materialCount, 1, 3);  //ambient
		BufferedImage sp = new BufferedImage(materialCount, 1, 3);  //Specular
		BufferedImage sh = new BufferedImage(materialCount, 1, 3);  //Shininess
		int index = 0;
		for(MaterialType m_ : material) {
			Material m = (Material)m_;
			Color d = new Color(m.getDiffuseC().x, m.getDiffuseC().y, m.getDiffuseC().z);
			Color a = new Color(m.getAmbientC().x, m.getAmbientC().y, m.getAmbientC().z);
			Color s = new Color(m.getSpecularC().x, m.getSpecularC().y, m.getSpecularC().z);
			Color h = new Color(m.getSpecularS(), m.getEmmisiveI(), m.getMetallicI());
			df.setRGBA(index, 0, d.getRGBA());
			am.setRGBA(index, 0, a.getRGBA());
			sp.setRGBA(index, 0, s.getRGBA());
			sh.setRGBA(index, 0, h.getRGBA());
			index++;
		}
		if(this.textures != null)
			this.textures.free();
		this.textures = new TextureArray(df, am, sp, sh);
	}
	
	/**Convert PBR materials to texture**/
	private void CPBRMTT() {
		int materialCount = material.length;
		BufferedImage al = new BufferedImage(materialCount, 1, 3);  //albedo
		BufferedImage me = new BufferedImage(materialCount, 1, 3);  //metallic
		BufferedImage ro = new BufferedImage(materialCount, 1, 3);  //roughness
		BufferedImage em = new BufferedImage(materialCount, 1, 3);  //emissive
		int index = 0;
		for(MaterialType m_ : material) {
			PBRMaterial m = (PBRMaterial)m_;
			Color albedo = new Color(m.getAlbedo().x, m.getAlbedo().y, m.getAlbedo().z, m.getAlbedo().w);
			Color metal = new Color(m.getMetallic(), m.getMetallic(), m.getMetallic());
			Color rough = new Color(m.getRoughness(), m.getRoughness(), m.getRoughness());
			Color emm = new Color(m.getEmissive(), m.getEmissive(), m.getEmissive());
			al.setRGBA(index, 0, albedo.getRGBA());
			me.setRGBA(index, 0, metal.getRGBA());
			ro.setRGBA(index, 0, rough.getRGBA());
			em.setRGBA(index, 0, emm.getRGBA());
			index++;
		}
		if(this.textures != null)
			this.textures.free();
		this.textures = new TextureArray(al, me, ro, em);
	}
	
	//#####################GETTERS/SETTERS###################################
	/**
	 * binds the Diffuse and Material Textures
	 */
	public void bind(int sampler) {
		for(int i = 0; i < 8; i++) {
			glActiveTexture(GL_TEXTURE0 + i);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		if(sampler >= 0 && sampler <= 31) {
			this.textures.bind(sampler);
		}
	}
	
	public void generateMipmaps() {
		bind(0);
		GL33.glGenerateMipmap(GL_TEXTURE_2D);
	}
	
	public MaterialType[] getMaterials() {
		return material;
	}
	public MaterialType getMaterial() {
		return material[0];
	}
	public void setMaterial(MaterialType material) {
		this.material[0] = material;
	}
	public MaterialType getMaterial(int index) {
		return material[index];
	}
	public void setMaterial(Material material, int index) {
		this.material[index] = material;
	}
	
	
	@Override
	public void free() {
		this.textures.free();
	}
	
	@Override
	public int getAmount() {
		return textures.getAmount();
	}
	@Override
	public HashMap<String, Integer> getFlags() {
		flags.clear();
		flags.put("repetetion", repetetion);
		return flags;
	}
	@Override
	public int getID(int index) {
		return textures.getID(index);
	}
	
	@Override
	public void setID(int index, int value) {
		this.textures.setID(index, value);
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
