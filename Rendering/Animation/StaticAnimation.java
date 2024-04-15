package Kartoffel.Licht.Rendering.Animation;

import org.joml.Matrix4f;

import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Tools.Tools;

public class StaticAnimation implements namable, Animation{
	
	private String name = "Static Animation";
	private final Matrix4f[] transforms;
	private final Matrix4f[] offsets;
	private final int BONES_AMOUNT;
	private final int[] PARENT_INDICES;
	
	public StaticAnimation(SkeletalAnimation animation, int frame) {
		transforms = new Matrix4f[animation.BONES_AMOUNT];
		for(int i = 0; i < animation.BONES_AMOUNT; i++)
			transforms[i] = new Matrix4f();
		BONES_AMOUNT = animation.BONES_AMOUNT;
		PARENT_INDICES = animation.PARENT_INDICES;
		offsets = animation.OFFSETS;
	}

	@Override
	public void getTransformations(Shader sh) {
		Matrix4f mat = new Matrix4f();
		for(int i = 0; i < BONES_AMOUNT; i++) {
			mat.identity();
			rec(mat, i);
			sh.setUniformMatrix4f("anim_0["+i+"]", mat);
		}
		sh.setUniformFloat("anim_time", 0);
	}
	
	private Matrix4f rec(Matrix4f mat, int id) {
		if(id == -1)
			return mat;
		Matrix4f toOrigin = offsets[id] != null ? offsets[id] : Tools.IDENTITY_MATRIX;
		Matrix4f bfOrigin = new Matrix4f(toOrigin).invert();
		return rec(mat, PARENT_INDICES[id]).mul(bfOrigin).mul(transforms[id]).mul(toOrigin);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTransform(int index, Matrix4f transform) {
		transforms[index] = transform;
	}
	public Matrix4f copyTransform(int index, Matrix4f dest) {
		return dest.set(transforms[index]);
	}
	public Matrix4f getTransform(int index) {
		return transforms[index];
	}
	public int getTransformAmount() {
		return transforms.length;
	}

	

}
