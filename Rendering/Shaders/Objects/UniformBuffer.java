package Kartoffel.Licht.Rendering.Shaders.Objects;

import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.freeable;

public class UniformBuffer implements freeable{
	
	private int id;
	private float[] data;

	public UniformBuffer(float[] data) {
		id = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, id);
		this.data = data;
		GL33.glBufferData(GL33.GL_UNIFORM_BUFFER, data, GL33.GL_STATIC_DRAW);
		
	}
	
	
	public final void bindUniformBlock(int binding) {
		GL33.glBindBufferBase(GL33.GL_UNIFORM_BUFFER, binding, id);
	}
	
	public void bind() {
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, id);
	}
	
	public float[] getData() {
		return data;
	}
	
	public void setData(float[] data) {
		this.data = data;
	}
	
	public void updateData() {
		bind();
		GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, 0, data);
	}
	
	public void updateData(int start, int end) {
		bind();
		float[] d = new float[end-start];
		System.arraycopy(data, start, d, 0, end-start);
		GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, start, d);
	}

	@Override
	public void free() {
		GL33.glDeleteBuffers(id);
	}
	
	public int getId() {
		return id;
	}


	public void bindNullBuffer() {
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}

}
