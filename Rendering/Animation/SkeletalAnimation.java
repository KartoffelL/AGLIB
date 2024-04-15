package Kartoffel.Licht.Rendering.Animation;

import java.io.Serializable;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AINode;

import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;

/**
 * A Skeletal animation. 
 *
 */
public class SkeletalAnimation implements namable, Animation, Serializable{
	private static final long serialVersionUID = 2716807995620864014L;

	public String name = "Skeletal Animation";
	
	/**
	 * Name
	 */
	public final String NAME;
	/**
	 * Duration of the animation in ticks
	 */
	public final double DURATION;
	/**
	 * Speed
	 */
	public final double TICKS_PER_SECOND;
	/*
	 * approximated memory usage in KB
	 */
	public final double MEMORY;
	/**
	 * Amount of Keyframes
	 */
	public int KEYFRAME_AMOUNT;
	/**
	 * Amount of Bones affected by the animation
	 */
	public int BONES_AMOUNT;
	
	//Storage[Frame][Bone]
	public final Matrix4f[][] FRAMES;
	
	public int[] PARENT_INDICES;
	public Matrix4f[] OFFSETS;
	
	public int rootID = -1;
	
	private Skeleton skelet;
	
	public SkeletalAnimation(String name, double duration, double tps, Matrix4f[][] frames, Skeleton skelet) {
		this.DURATION = duration;
		this.TICKS_PER_SECOND = tps;
		this.NAME = name;
		this.skelet = skelet;
		this.FRAMES = parseFrames(frames);
		int memory = 0;
		for(int x = 0; x < FRAMES.length; x++) {
			Matrix4f[] f = FRAMES[x];
			for(int y = 0; y < f.length; y++) {
				memory += 4*16; //16 x floats
				memory += 8; //Serial ID as specified in Matrix4f.class;
				memory += 4; //Properties as specified in Matrix4f.class;
			}
		}
		MEMORY = memory*0.001;
		Tools.Ressource("Found Animation '" + name + "' " + duration + "t@" + tps + "t/s. ["+frames.length+"B;"+frames[0].length+"KF] -> " + MEMORY + "KB");
		skelet = null;
	}
	
	/**
	 * Assuming All KeyFrames contain all Bones/All bones exists in all KeyFrames;
	 * @param in
	 * @return
	 */
	public Matrix4f[][] parseFrames(Matrix4f[][] in) {
		//Setting amount of Bones
		BONES_AMOUNT = in.length;
		//Evaluating amount of KeyFrames.
		KEYFRAME_AMOUNT = 0;
		for(int a = 0; a < BONES_AMOUNT; a++)
			if(in[a].length > KEYFRAME_AMOUNT)
				KEYFRAME_AMOUNT = in[a].length;
		
		//Creating the output array
		Matrix4f[][] out = new Matrix4f[KEYFRAME_AMOUNT][BONES_AMOUNT];
		PARENT_INDICES = new int[BONES_AMOUNT];
		OFFSETS = new Matrix4f[BONES_AMOUNT];
		
		for(int i = 0; i < KEYFRAME_AMOUNT; i++) {
			for(int l = 0; l < BONES_AMOUNT; l++) {
				if(in[l].length > i) //If this bone has data for this Keyframe
					out[i][l] = in[l][i];
				else //If bone is lacking data. Maybe should be 'Skeleton.matrix(skelet.getBone(l).mNode().mTransformation())'
					out[i][l] = new Matrix4f().identity();
			}
		}
		//prasing matrices
		for(int i = 0; i < KEYFRAME_AMOUNT; i++) {
			out[i] = parseFrame(out[i]);
		}
		//Fill in the gaps of possibly un-animated Bones.
		for(int l = 0; l < out.length; l++) {
			for(int i = 0; i < BONES_AMOUNT; i++)
				if(out[l][i] == null)
					out[l][i] = new Matrix4f().identity();
		}
		return out;
	}
	private Matrix4f[] parseFrame(Matrix4f[] in) {
		Matrix4f[] out = new Matrix4f[in.length];
		parseBonesRec(in, out, skelet.root, new Matrix4f());
		return out;
	}
	
	private void parseBonesRec(Matrix4f[] offsets, Matrix4f[] out, AINode t, Matrix4f mat) {
		int ID = skelet.containsBoneID(t.mName().dataString());
		int parentID = -1;
		if(t.mParent() != null)
			parentID = skelet.containsBoneID(t.mParent().mName().dataString());
		if(ID != -1) {
			PARENT_INDICES[ID] = parentID; //Is -1 if bone is Root.
			if(rootID == -1)
				rootID = ID;
			mat.mul(offsets[ID]);
			Matrix4f o = Skeleton.matrix(skelet.getBone(ID).mOffsetMatrix());
			OFFSETS[ID] = new Matrix4f(o);
			out[ID] = mat.mul(o, o);
		}
		for(int i = 0; i < t.mNumChildren(); i++)
			parseBonesRec(offsets, out, AINode.create(t.mChildren().get(i)), new Matrix4f(mat));
	}
	
	
	public double getFrame() {
		double timeS = Timer.getTimeMilli()/1000.0;
		double timeT = timeS*TICKS_PER_SECOND;
		return timeT % FRAMES.length;
	}
	
	public void getTransformations(Shader s) {
		double t = getFrame();
		int frame = (int)t;
		float fract = (float) (t-frame);
		for(int i = 0; i < BONES_AMOUNT; i++) {
			s.setUniformMatrix4f("anim_0["+i+"]", FRAMES[frame][i]);
			s.setUniformMatrix4f("anim_1["+i+"]", FRAMES[(frame+1) % FRAMES.length][i]);
		}
		s.setUniformFloat("anim_time", fract);
	}


	public static void getDefaultTransformations(Shader s) {
		s.setUniformFloat("anim_time", -1);
	}
	
	@Override
	public String toString() {
		return "{["+BONES_AMOUNT+"B;"+KEYFRAME_AMOUNT+"K]: "+Tools.format(DURATION/TICKS_PER_SECOND)+"s@"+TICKS_PER_SECOND+"tps}";
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
