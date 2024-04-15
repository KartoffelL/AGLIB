package Kartoffel.Licht.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Vector3f;

import Kartoffel.Licht.Rendering.Texture.Material.Material;
import Kartoffel.Licht.Res.FileLoader;

public class MaterialLoader {

	
	private HashMap<String, Material> materials = new HashMap<String, Material>();
	private List<Material> materialList = new ArrayList<Material>();
	
	/**
	 * Empty loader.
	 */
	public MaterialLoader() {
		
	}
	
	public MaterialLoader(String f) {
		load(f);
		Tools.Ressource("loaded " + materials.values().size() + " materials");
		for(String m : materials.keySet()) {
			System.out.println("--"+m);
			Material ma = materials.get(m);
			Tools.Ressource("----Ambient Color: " + ma.getAmbientC());
			Tools.Ressource("----Diffuse Color: " + ma.getDiffuseC());
			Tools.Ressource("----Specular Color: " + ma.getSpecularC());
			Tools.Ressource("----Shine: " + ma.getSpecularS());
			Tools.Ressource("----Illum: " + ma.getIllum());
		}
		for(Material mat : materials.values())
			materialList.add(mat);
	}
	
	private void load(String f) {
		try {
			List<String> lines = readLines(FileLoader.getFileD(f));
			int i = 0;
			for(;i < lines.size(); i++) {
				String l = lines.get(i);
				if(l.startsWith("#"))
					continue;
				if(l.startsWith("newmtl ")) {
					//Material start #######################
					String[] nma = l.split(" ");
					if(nma.length > 1) {
						String matName = nma[1];
						Material mat = new Material();
						for(;i < lines.size(); i++) {
							l = lines.get(i);
							if(l.equalsIgnoreCase(""))
								break;
							if(l.startsWith("Ka")) {
								String[] c = l.split(" ");
								if(c.length > 3) {
									mat.setAmbient(new Vector3f(Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3])));
								}
							}
							if(l.startsWith("Kd")) {
								String[] c = l.split(" ");
								if(c.length > 3) {
									mat.setDiffuse(new Vector3f(Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3])));
								}				
							}
							if(l.startsWith("Ns")) {
								String[] c = l.split(" ");
								if(c.length > 1) {
									mat.setSpecularS(Float.parseFloat(c[1]));
								}
							}
							if(l.startsWith("Ks")) {
								String[] c = l.split(" ");
								if(c.length > 3) {
									mat.setSpecular(new Vector3f(Float.parseFloat(c[1]), Float.parseFloat(c[2]), Float.parseFloat(c[3])));
								}
							}
							if(l.startsWith("illum")) {
								String[] c = l.split(" ");
								if(c.length > 1) {
									mat.setIllum(Integer.parseInt(c[1]));
								}
							}
						}
						materials.put(matName, mat);
					}
					//Material end ##########################
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void sync(List<String> materials) {
		this.materialList.clear();
		for(String s : materials) {
			this.materialList.add(this.materials.get(s));
		}
	}
	/**
	 * Puts all registered Materials of ml into this MaterialLoader.<br>
	 * Overrides existing materials
	 * @param ml
	 */
	public void merge(MaterialLoader ml) {
		this.materials.putAll(ml.getMaterialMap());
	}

	
	public static List<String> readLines(InputStream f) throws IOException {
		
		List<String> l = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(f));
		String line;
		while((line = br.readLine()) != null) {
			l.add(line);
		}
		br.close();
		return l;
	}

	public List<Material> getMaterials() {
		return materialList;
	}
	
	public HashMap<String, Material> getMaterialMap() {
		return this.materials;
	}

}
