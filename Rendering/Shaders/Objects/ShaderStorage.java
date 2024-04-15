package Kartoffel.Licht.Rendering.Shaders.Objects;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;

import Kartoffel.Licht.Java.freeable;

public class ShaderStorage implements freeable{
	
	private int id;
	private float[] data;

	public ShaderStorage(float[] data) {
		id = GL33.glGenBuffers();
		GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, id);
		this.data = data;
		GL33.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, data, GL33.GL_DYNAMIC_DRAW);
	}
	
	
	public final void bindUniformBlock(int binding) {
		GL33.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, binding, id);
	}
	
	public void bind() {
		GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, id);
		
	}
	
	public float[] getData() {
		return data;
	}
	
	public void setData(float[] data) {
		this.data = data;
	}
	
	public void updateData() {
		bind();
		GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
		GL33.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, data);
	}
	public void updateSubData(float[] data, long offset) {
		bind();
		GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
		GL33.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, offset*4, data);
	}
	
	public void updateSubData(int start, int end) {
		bind();
		float[] d = new float[end-start];
		System.arraycopy(data, start, d, 0, end-start);
		GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
		GL33.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, start*4, d);
	}
	
	public void updateSubData(int start, int end, float[] d) {
		bind();
		System.arraycopy(data, start, d, 0, end-start);
		GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
		GL33.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, start*4, d);
	}

	@Override
	public void free() {
		GL33.glDeleteBuffers(id);
	}
	
	public int getId() {
		return id;
	}


	public void bindNullBuffer() {
		GL33.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}

}
