package Kartoffel.Licht.Tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIFace.Buffer;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Rendering.Animation.SkeletalAnimation;
import Kartoffel.Licht.Rendering.Animation.Skeleton;
import Kartoffel.Licht.Rendering.Shapes.Shape;
import Kartoffel.Licht.Rendering.Texture.Material.Material;

public class AssimpLoader extends Loader{
	private static final long serialVersionUID = 6771639163663145701L;


	public AssimpLoader(InputStream in) {
		//Simple one-File loading
		ByteBuffer sceneBuffer = null;
		try {
			byte[] bytes = in.readAllBytes();
			sceneBuffer = MemoryUtil.memAlloc(bytes.length);
			sceneBuffer.put(bytes);
			sceneBuffer.flip();
		} catch (IOException e) {
			Tools.err("Unable to load Model: " + e.getMessage());
			return;
		}
		this.load(sceneBuffer, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_PopulateArmatureData | Assimp.aiProcess_EmbedTextures | Assimp.aiProcess_PreTransformVertices | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_OptimizeGraph);
	}
	public AssimpLoader(InputStream in, Skeleton skelet) {
		if(skelet != null)
			this.skelet = skelet;
		//Simple one-File loading
		ByteBuffer sceneBuffer = null;
			try {
			byte[] bytes = in.readAllBytes();
			sceneBuffer = MemoryUtil.memAlloc(bytes.length);
			sceneBuffer.put(bytes);
			sceneBuffer.flip();
		} catch (IOException e) {
			Tools.err("Unable to load Model: " + e.getMessage());
			return;
		}
		this.load(sceneBuffer, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_PopulateArmatureData | Assimp.aiProcess_EmbedTextures | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_OptimizeGraph);
	}
	
	public AssimpLoader(ByteBuffer scene) {
		this.load(scene, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals | Assimp.aiProcess_PopulateArmatureData | Assimp.aiProcess_EmbedTextures | Assimp.aiProcess_PreTransformVertices | Assimp.aiProcess_OptimizeMeshes | Assimp.aiProcess_OptimizeGraph);
	}
	public Skeleton skelet = new Skeleton();
	public SkeletalAnimation[] animations;
	
	public BufferedImage[] embedTextures;
	public String[] embedPaths;
	private String[][] textureAssignments;
	
	
	public AINode root;
	
	
	public void load(ByteBuffer sceneBuffer, int flags) {
		Tools.Ressource("#####Loading Model#####");
		AIScene scene = Assimp.aiImportFileFromMemory(sceneBuffer, flags, "");
		//SystemIO implementation
//		AIFileIO fileIo = AIFileIO.create()
//				//When File opened
//	            .OpenProc((pFileIO, fileName, openMode) -> {
//	            	//Files content data
//	            	ByteBuffer data = FileLoader.getBuffer(MemoryUtil.memUTF8(fileName));
//	            	//AIFile
//					return AIFile.create()
//							//When read
//	                    .ReadProc((pFile, pBuffer, size, count) -> {
//	                        long max = Math.min(data.remaining(), size * count);
//	                        MemoryUtil.memCopy(MemoryUtil.memAddress(data) + data.position(), pBuffer, max);
//	                        return max;
//	                    })
//	                    //Seek idk
//	                    .SeekProc((pFile, offset, origin) -> {
//	                        if (origin == Assimp.aiOrigin_CUR) {
//	                            data.position(data.position() + (int) offset);
//	                        } else if (origin == Assimp.aiOrigin_SET) {
//	                            data.position((int) offset);
//	                        } else if (origin == Assimp.aiOrigin_END) {
//	                            data.position(data.limit() + (int) offset);
//	                        }
//	                        return 0;
//	                    })
//	                    //File Size
//	                    .FileSizeProc(pFile -> data.limit())
//	                    //Gets the addres of this File
//	                    .address();
//	            })
//	            //When File closed
//	            .CloseProc((pFileIO, pFile) -> {
//	                AIFile aiFile = AIFile.create(pFile);
//	                aiFile.ReadProc().free();
//	                aiFile.SeekProc().free();
//	                aiFile.FileSizeProc().free();
//	            });
		//AIScene scene = Assimp.aiImportFileEx(file, flags, fileIo);
//		AIScene scene = Assimp.aiImportFile(FileLoader.getD(file), flags);
		
		if(scene == null) {
			Tools.err("Error loading Assimp model! " + Assimp.aiGetErrorString());
			return;
		}
		else
			Tools.Ressource("Errors: " + Assimp.aiGetErrorString() == "" ? "-" : Assimp.aiGetErrorString());
		
		int numMeshes = scene.mNumMeshes();
		PointerBuffer aiMeshes = scene.mMeshes();
		Shape[] shapes = new Shape[numMeshes];
		//Skeleton for animations
		List<AIBone> bones = new ArrayList<AIBone>();
		Tools.Ressource("Loading " + numMeshes + " Meshes...");
		//For every Mesh in this Scene.
		//all Meshes are given a Shape, but they are going to be merged later on.
		for (int i = 0; i < numMeshes; i++) {
		    AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
		    shapes[i] = new Shape();
		    //Vertices
		    int Vsize = aiMesh.mNumVertices();
		    org.lwjgl.assimp.AIVector3D.Buffer Vb = aiMesh.mVertices();
		    shapes[i].ver = new float[Vsize*3];
		    
		    for(int l = 0; l < Vsize; l++) {
		    	AIVector3D v = Vb.get(l);
		    	shapes[i].ver[l*3+0] = v.x();
		    	shapes[i].ver[l*3+1] = v.y();
		    	shapes[i].ver[l*3+2] = v.z();
		    }
		    //Indices
		    int Isize = aiMesh.mNumFaces();
		    Buffer Ib = aiMesh.mFaces();
		    List<Integer> IndList = new ArrayList<>();
		    int Icount = 0;
		    for(int l = 0; l < Isize; l++) {
		    	AIFace v = Ib.get(l);
		    	Icount += v.mNumIndices(); //SHould be 3 (Triangle)
		    	for(int aaa = 0; aaa < v.mIndices().capacity(); aaa++)
		    		IndList.add(v.mIndices().get(aaa));
		    }
		    shapes[i].ind = new int[Icount];
		    for(int fff = 0; fff < Icount; fff++)
		    	shapes[i].ind[fff] = IndList.get(fff);
		    
		    //Normals
		    org.lwjgl.assimp.AIVector3D.Buffer Nb = aiMesh.mNormals();
		    shapes[i].nor = new float[Vsize*3];
		    for(int ggg = 0; ggg < Vsize; ggg++) {
		    	AIVector3D v = Nb.get(ggg);
		    	shapes[i].nor[ggg*3+0] = v.x();
		    	shapes[i].nor[ggg*3+1] = v.y();
		    	shapes[i].nor[ggg*3+2] = v.z();
		    }
		    
		    //Texture
		    shapes[i].tex = new float[Vsize*2];
		    org.lwjgl.assimp.AIVector3D.Buffer Tb = aiMesh.mTextureCoords(0);
		    if(Tb == null) {
		    	for(int a = 0; a < Vsize; a++) {
			    	 shapes[i].tex[a*2+0] = 0;
			    	 shapes[i].tex[a*2+1] = 0;
			    }
		    }
		    else {
			    for(int a = 0; a < Vsize; a++) {
			    	 shapes[i].tex[a*2+0] = Tb.get(a).x();
			    	 shapes[i].tex[a*2+1] = 1-Tb.get(a).y();
			    }
		    }
		    //Materials
		    shapes[i].mat = new int[Vsize];
		    for(int h = 0; h < Vsize; h++)
		    	shapes[i].mat[h] = aiMesh.mMaterialIndex();
		    
		    
		    //Bones
		    @SuppressWarnings("unchecked")
			List<Object[]>[] weights = new List[Vsize];
		    for(int h = 0; h < Vsize; h++) {
		    	weights[h] = new ArrayList<Object[]>();
		    }
		    PointerBuffer pb = aiMesh.mBones();
		    
		    //Parse Bones
		    for(int b = 0; b < aiMesh.mNumBones(); b++) {
		    	AIBone bone = AIBone.create(pb.get(b));
		    	bones.add(bone);
		    	//Weights
		    	org.lwjgl.assimp.AIVertexWeight.Buffer weig = bone.mWeights();
		    	for(int c = 0; c < bone.mNumWeights(); c++) {
		    		if(weig.get(c).mWeight() != 0)
		    			weights[weig.get(c).mVertexId()].add(new Object[] {bone, weig.get(c).mWeight()});
		    	}
		    	
		    }
		    //Bones
		    shapes[i].BoneIds = new int[Vsize*4];
		    shapes[i].BoneWeights = new float[Vsize*4];
		    for(int g = 0; g < Vsize; g++) {
		    	for(int v = 0; v < Math.min(weights[g].size(), 4); v++) {
		    		shapes[i].BoneIds[g*4+v] = skelet.getBoneID(((AIBone) weights[g].get(v)[0]).mNode().mName().dataString());
			    	shapes[i].BoneWeights[g*4+v] = (float) weights[g].get(v)[1];
		    	}
		    }
		    // the mesh
		    Tools.Ressource("Parsed Mesh " + i + ". Total: " + shapes[i].ver.length + "V, " + shapes[i].ind.length + "I, " + aiMesh.mNumBones() + "B");
		}
		skelet.bones.addAll(bones);
		root = scene.mRootNode();
		skelet.root = root;
		//Merging the Shapes
		for(Shape s : shapes)
			this.add(s);
		this.shapes = shapes;
		Tools.Ressource(this.ver == null ? "No Vertices loaded" : "Total Vertices: " + this.ver.length);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		//Materials/Textures/Colors. 
		Tools.Ressource("Loading " + scene.mNumTextures() + " Textures...");
		embedTextures = new BufferedImage[scene.mNumTextures()];
		embedPaths = new String[embedTextures.length];
		for(int i = 0; i < scene.mNumTextures(); i++) {
			AITexture t = AITexture.create(scene.mTextures().get(i));
			BufferedImage bi = null;
			ByteBuffer bb = t.pcDataCompressed();
			byte[] arr = new byte[bb.capacity()];
			for(int a = 0; a < arr.length; a++) {
				arr[a] = bb.get(a);
			}
			bi = ImageIO.read(new ByteArrayInputStream(arr), 4);
			embedTextures[i] = bi;
			embedPaths[i] = t.mFilename().dataString();
			if(bi != null)
				Tools.Ressource("Parsed Texture " + i + "("+t.mFilename().dataString()+"): " + bi.getWidth() + " " + bi.getHeight());
			else
				Tools.Ressource("Failed to parse Texture " + i  + "("+t.mFilename()+"!");
		}
		
		
		int numMaterials = scene.mNumMaterials();
		textureAssignments = new String[numMaterials][4];
		Tools.Ressource("Loading " + numMaterials + " Materials...");
		PointerBuffer aiMaterials = scene.mMaterials();
		AIColor4D colour = AIColor4D.create();
		this.materials = new Material[numMaterials];
		
		for (int i = 0; i < numMaterials; i++) {
		    AIMaterial mat = AIMaterial.create(aiMaterials.get(i));
		    
		    //Textures
		    AIString path = AIString.calloc();
		    
		    Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
		    textureAssignments[i][0] = path.dataString();
		    
		    Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_METALNESS, 0, path, (IntBuffer) null, null, null, null, null, null);
		    textureAssignments[i][1] = path.dataString();
		    
		    Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_DIFFUSE_ROUGHNESS, 0, path, (IntBuffer) null, null, null, null, null, null);
		    textureAssignments[i][2] = path.dataString();
		    
		    Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_EMISSIVE, 0, path, (IntBuffer) null, null, null, null, null, null);
		    textureAssignments[i][3] = path.dataString();
		    
		    //--------------------------
		    
		    Material m = new Material();
		    //Ambient
		    int result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setAmbient(new Vector3f(colour.r(), colour.g(), colour.b()));
		    //Diffuse
		    result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setDiffuse(new Vector3f(colour.r(), colour.g(), colour.b()));
		    //Specular
		    result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setSpecular(new Vector3f(colour.r(), colour.g(), colour.b()));
		    //Specular factor
		    result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_SPECULAR_FACTOR, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setSpecularS(colour.r());
		    //Emmisive
		    result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_COLOR_EMISSIVE, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setEmmisiveC(new Vector3f(colour.r(), colour.g(), colour.b()));
		    //Emmisive factor
		    result = Assimp.aiGetMaterialColor(mat, Assimp.AI_MATKEY_EMISSIVE_INTENSITY, Assimp.aiTextureType_NONE, 0, colour);
		    if(result != -1)
		    	m.setEmmisiveI(colour.r());
		    
		    Tools.Ressource("Parsed Material: " + m.toString());
		    
		    //Setting the material
		    this.materials[i] = m;
		}
		this.matl = numMaterials;
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		//Animations
		animations = new SkeletalAnimation[scene.mNumAnimations()];
		for(int i = 0; i < scene.mNumAnimations(); i++) {
			AIAnimation anim = AIAnimation.create(scene.mAnimations().get(i));
			//Creating an Animation path for every bone. Channel count may not be the same.
			Matrix4f[][] matrices = new Matrix4f[skelet.bones.size()][0];
			//For every channel (Per bone)
			for(int l = 0; l < anim.mNumChannels(); l++) {
				//Create an AINodeAnim instance
				AINodeAnim anode = AINodeAnim.create(anim.mChannels().get(l));
				//Get the Bone ID using the name of the AINodeAnim
				int boneID = skelet.getBoneID(anode.mNodeName().dataString());
				//Create an Array for all KeyFrames.
				Matrix4f[] v = new Matrix4f[Math.max(Math.max(anode.mNumPositionKeys(), anode.mNumRotationKeys()), anode.mNumScalingKeys())];
				//Set up every Matrix in the array
				for(int k = 0; k < v.length; k++) {
					v[k] = new Matrix4f().identity();
				}
				//loop for all position KeyFrames and apply them
				for(int a = 0; a < anode.mNumPositionKeys(); a++) {
					v[a].translate(
							anode.mPositionKeys().get(a).mValue().x(),
							anode.mPositionKeys().get(a).mValue().y(),
							anode.mPositionKeys().get(a).mValue().z()
							);
				}
				//loop for all Rotation KeyFrames and apply them
				for(int a = 0; a < anode.mNumRotationKeys(); a++) {
					v[a].rotate(new Quaternionf(
							anode.mRotationKeys().get(a).mValue().x(),
							anode.mRotationKeys().get(a).mValue().y(),
							anode.mRotationKeys().get(a).mValue().z(),
							anode.mRotationKeys().get(a).mValue().w()
							).normalize());
				}
				//loop for all Size KeyFrames and apply them
				for(int a = 0; a < anode.mNumScalingKeys(); a++) {
					v[a].scale(
							anode.mScalingKeys().get(a).mValue().x(),
							anode.mScalingKeys().get(a).mValue().y(),
							anode.mScalingKeys().get(a).mValue().z()
							);
				}
				//Put the KeyFrame array inside the animation array.
				matrices[boneID] = v;
			}
			SkeletalAnimation sa = new SkeletalAnimation(anim.mName().dataString(), anim.mDuration(), anim.mTicksPerSecond(), matrices, skelet);
			animations[i] = sa;
		}
		root =null;
		skelet = null;
		Tools.Ressource("#########################");
	}
	
	public Shape[] getShapes() {
		return shapes;
	}
	
	public Material[] getMaterials() {
		return this.materials;
	}

	public BufferedImage[] packTextures() {
		BufferedImage[] imgs = new BufferedImage[4];
		int width = embedTextures[0].getWidth();
		for(int i = 0; i < 4; i++) {
			imgs[i] = new BufferedImage(width*this.matl, embedTextures[0].getHeight(), 2);
			for(int l = 0; l < matl; l++) {
				String p = textureAssignments[l][i];
				int k = 0;
				while(k < embedPaths.length && !embedPaths[k].equalsIgnoreCase(p))
					k++;
					
				imgs[i].scaleDraw(embedTextures[k], l*width, 0, width, embedTextures[k].getHeight());
			}
		}
		return imgs;
	}
}
