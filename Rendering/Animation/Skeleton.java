package Kartoffel.Licht.Rendering.Animation;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;

public class Skeleton {
	public static Matrix4f matrix(AIMatrix4x4 mat) {
		Matrix4f matrix = new Matrix4f();
		matrix.m00(mat.a1()).m01(mat.a2()).m02(mat.a3()).m03(mat.a4());
		matrix.m10(mat.b1()).m11(mat.b2()).m12(mat.b3()).m13(mat.b4());
		matrix.m20(mat.c1()).m21(mat.c2()).m22(mat.c3()).m23(mat.c4());
		matrix.m30(mat.d1()).m31(mat.d2()).m32(mat.d3()).m33(mat.d4());
		return matrix.transpose();
	}
	
	public List<AIBone> bones = new ArrayList<AIBone>();
	public AINode root;
	
	private List<String> bids = new ArrayList<String>();
	public int getBoneID(String s) {
		boolean has = false;
		for(int i = 0; i < bids.size(); i++)
			if(s.equalsIgnoreCase(bids.get(i)))
				has = true;
		if(has)
			return bids.indexOf(s);
		bids.add(s);
		return bids.size()-1;
	}
	public int containsBoneID(String s) {
		boolean has = false;
		for(int i = 0; i < bids.size(); i++)
			if(s.equalsIgnoreCase(bids.get(i)))
				has = true;
		if(has)
			return bids.indexOf(s);
		return -1;
	}

	public AIBone getBone(int ID) {
		for(AIBone b : bones) {
			if(containsBoneID(b.mName().dataString()) == ID)
				return b;
		}
		return null;
	}
}

