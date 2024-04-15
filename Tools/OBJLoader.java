package Kartoffel.Licht.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

import Kartoffel.Licht.Rendering.Texture.Material.Material;
import Kartoffel.Licht.Res.FileLoader;

public class OBJLoader extends Loader{
	private static final long serialVersionUID = -4194095649375118750L;

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
	/**
	 * Loads a OBJ File, does some magic, and returns the vertices, texture coordinates, normals, indices and material indices.<br>
	 * For use with materials, use OBJLoader.OBJLoader(String f, MaterialLoader ml)
	 * @param f - the path
	 * @throws IOException 
	 */
	public OBJLoader(InputStream data) throws IOException {
		load(List.of(new String(data.readAllBytes()).split("\n")), null);
	}
	
	private void load(List<String> lines, String f) {

		List<Vector3f> vert = new ArrayList<Vector3f>();
		List<Vector3f> normal = new ArrayList<Vector3f>();
		List<Vector2f> textu = new ArrayList<Vector2f>();
		List<Vector4i> faces = new ArrayList<Vector4i>();
		
		List<String> materials = new ArrayList<String>();
		List<MaterialLoader> loaders = new ArrayList<MaterialLoader>();
		
		String mat = "default";
		for(String line : lines) {
			String[] tokens = line.split("\\s");
			switch (tokens[0]) {
				case "v":
					//vertexies
					Vector3f v = new Vector3f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3])
							);
					vert.add(v);
					break;
				case "vt":
					//Texture
					Vector2f vt = new Vector2f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2])
							);
					textu.add(vt);
					break;
				case "vn":
					//Normals
					Vector3f vn = new Vector3f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3])
							);
					normal.add(vn);
					break;
				case "f":
					//Faces
					processFace(tokens[1], faces, materials.indexOf(mat));
					processFace(tokens[2], faces, materials.indexOf(mat));
					processFace(tokens[3], faces, materials.indexOf(mat));
					break;
				case "usemtl":
					mat = tokens[1];
					if(!materials.contains(mat))
						materials.add(mat);
				case "mtllib":
					//System.err.println(Tools.getDir(f)+tokens[1]);
					if(f != null)
						if(FileLoader.getFileD(Tools.getFileDir(f)+tokens[1]) != null)
							loaders.add(new MaterialLoader(Tools.getFileDir(f)+tokens[1]));
				default:
					break;
			}
		}
		List<Integer> indices = new ArrayList<Integer>();
		float[] vertarr = new float[vert.size()*3];
		int i = 0;
		for(Vector3f pos : vert) {
			vertarr[i*3 + 0] = pos.x;
			vertarr[i*3 + 1] = pos.y;
			vertarr[i*3 + 2] = pos.z;
			i++;
		}
		
		float[] texCoordarr = new float[vert.size() * 2];
		float[] normalarr = new float[vert.size() * 3];
		
		 int[] materialarr = new int[vert.size()];
		
		for(Vector4i face : faces) {
			processVertex(face.x, face.y, face.z, textu, normal, indices, texCoordarr, normalarr);
			materialarr[face.x] = face.w;
		}
		
		int[] indiarr = indices.stream().mapToInt((Integer v) -> v).toArray();
		
		//Materials
		MaterialLoader ml = new MaterialLoader();
		for(MaterialLoader mla : loaders) {
			ml.merge(mla);
		}
		ml.sync(materials);
		this.materials = (Material[]) ml.getMaterials().toArray(new Material[ml.getMaterials().size()]);
		//---------------------------
		this.ind = indiarr;
		this.nor = normalarr;
		this.tex = texCoordarr;
		this.ver = vertarr;
		this.mat = materialarr;
		this.matl = this.materials.length;
	
	}
	
	private static void processFace(String token, List<Vector4i> faces, int material) {
		String[] lineToken = token.split("/");
		int lenght = lineToken.length;
		int pos = -1, coords = -1, normal = -1;
		pos = Integer.parseInt(lineToken[0]) - 1;
		if(lenght > 1) {
			String textCoord = lineToken[1];
			coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
			if(lenght > 2) {
				normal = Integer.parseInt(lineToken[2]);
			}
		}
		Vector4i faceVec = new Vector4i(pos, coords, normal, material);
		faces.add(faceVec);
	}
	
	private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList,
										List<Vector3f> normalList, List<Integer> indicesList,
										float[] texCoordArr, float[] normalArr) {
					
		indicesList.add(pos);
		if(texCoord >= 0) {
			Vector2f texCoordVec = texCoordList.get(texCoord);
			texCoordArr[pos*2] = texCoordVec.x;
			texCoordArr[pos*2 + 1] = 1 - texCoordVec.y;
		}
		
		if(normal >= 0) {
			Vector3f normalVec = normalList.get(normal-1);
			normalArr[pos * 3] = normalVec.x;
			normalArr[pos * 3 + 1] = normalVec.y;
			normalArr[pos * 3 + 2] = normalVec.z;
		}							
											
	}
	
	
}
