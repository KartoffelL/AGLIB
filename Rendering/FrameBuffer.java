package Kartoffel.Licht.Rendering;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Java.opengl;
import Kartoffel.Licht.Rendering.Texture.FBOTexture;
import Kartoffel.Licht.Rendering.Texture.MultiTexture;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Tools.Tools;

/**
 * A Framebuffer is a buffer, which holds graphical data in form of a texture.<br>
 * If bound, everything is drawn to its bound texture instead of the default Framebuffer/Screen.
 * 
 * @author PC
 *
 */
@opengl
public class FrameBuffer implements freeable, namable{
	
	/**
	 * Doesn�t work
	 */
	public static int FLAG_MULTISAMPLED = 1;
	/**
	 * Automatically resizes the Buffer to the Windows size;
	 */
	public static int FLAG_AUTORESIZE = 2;
	
	public static int FLAG_AUTORESIZE_SSAA_X2 = 3;
	public static int FLAG_AUTORESIZE_SSAA_X4 = 4;
	public static int FLAG_AUTORESIZE_SSAA_X8 = 5;
	public static int FLAG_AUTORESIZE_SSAA_X16 = 6;
	
	public static int FLAG_MAGF_NEAREST = 9;
	public static int FLAG_MINF_NEAREST = 10;
	
	private String name = "FrameBuffer";
	
	
	private int id;
	private FBOTexture tex[];
	private FBOTexture depthStencilAll[];
	private FBOTexture depthStencil;
	private GraphicWindow renderer;
	private int sx, sy;
	private boolean multisample;
	private float mss = 1;
	private boolean autoR;
	public FrameBuffer(GraphicWindow renderer, int sx, int sy, int outputs, int...flags) {
		this(renderer, sx, sy, Tools.set(new int[outputs], 2), flags);
	}
	/**
	 * A Framebuffer is a buffer, which holds graphical data in form of a texture.<br>
	 * If bound, everything is drawn to its bound texture instead of the default Framebuffer/Screen.<br>
	 * 
	 * @param outputs how many HDR texture outputs the Framebuffer should have
	 * @param outputConfigs the sizes of each texture, ex.:<br>
	 * outputs = 2, outputConfigs = {1000, 1000, 100, 100}
	 * 
	 *
	 */
	public FrameBuffer(GraphicWindow renderer, int sx, int sy, int[] outputs, int...flags) {
		if(outputs.length < 0 || outputs.length > 31)
			return;
		this.renderer = renderer;
		this.sx = sx;
		this.sy = sy;
		
		multisample = Tools.containsFlag(flags, FLAG_MULTISAMPLED);
		autoR = Tools.containsFlag(flags, FLAG_AUTORESIZE);
		if(Tools.containsFlag(flags, FLAG_AUTORESIZE_SSAA_X2))
			mss = 2;
		else if(Tools.containsFlag(flags, FLAG_AUTORESIZE_SSAA_X4))
			mss = 4;
		else if(Tools.containsFlag(flags, FLAG_AUTORESIZE_SSAA_X8))
			mss = 8;
		else if(Tools.containsFlag(flags, FLAG_AUTORESIZE_SSAA_X16))
			mss = 16;
		int magfilter = GL33.GL_LINEAR;
		int minfilter = GL33.GL_LINEAR;
		if(Tools.containsFlag(flags, FLAG_MAGF_NEAREST))
			magfilter = GL33.GL_NEAREST;
		if(Tools.containsFlag(flags, FLAG_MINF_NEAREST))
			minfilter = GL33.GL_NEAREST;
		if(renderer != null)
			renderer.listeners.add(new GLFWFramebufferSizeCallback() {
				
				@Override
				public void invoke(long window, int width, int height) {
					if(isAutoResize())
						FrameBuffer.this.reSize((int)(width*mss), (int)(height*mss));
				}
			});
		id = glGenFramebuffers();
		bind();
		tex = new FBOTexture[outputs.length];
		
		int[] attachments = new int[outputs.length];
		
		for(int i = 0; i < outputs.length; i++) {
			tex[i] = new FBOTexture(sx, sy, outputs[i], multisample, minfilter, magfilter);
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, tex[i].getId(), 0); 
			attachments[i] = GL_COLOR_ATTACHMENT0+i;
		}
		//If there is no Color output 
		if(outputs.length == 0) {
			glDrawBuffer(GL_NONE);
			glReadBuffer(GL_NONE);
		}
		
		depthStencil = new FBOTexture(sx, sy, 1, false, minfilter, magfilter);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,  multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, depthStencil.getId(), 0);
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		if(status != GL_FRAMEBUFFER_COMPLETE) {
			Tools.err("FBO failed to generate! " + status);
		}
			
		glDrawBuffers(attachments);
		bindDefaultFrameBuffer();
		
		depthStencilAll = new FBOTexture[tex.length+1];
		for(int i = 0; i < tex.length; i++)
			depthStencilAll[i] = tex[i];
		
		depthStencilAll[tex.length] = depthStencil;
	}
	
	public void setTarget(Renderable renderable) {
		setTarget(renderable, true);
	}
	public void setTarget(Renderable[] renderable) {
		setTarget(renderable, true);
	}
	public void setTarget(Renderable[] renderables, boolean freeAll) {
		bind();
		if(freeAll)
		for(Renderable r : tex)
			r.free();
		int i = 0;
		for(Renderable renderable : renderables) {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, renderable.getID(0), 0); 
			i++;
		}
	}
	public void setTarget(Renderable renderable, boolean freeAll) {
		bind();
		if(freeAll)
			for(Renderable r : tex)
				r.free();
		if(renderable instanceof MultiTexture) {
			for(int i = 0; i < 32; i++) {
				if(i < ((MultiTexture) renderable).getAmount()) {
					glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, renderable.getID(i), 0); 
				}
				else {
					glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i, multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, renderable.getID(i), 0); 
				}
			}
		}
		else {
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, multisample ? GL33.GL_TEXTURE_2D_MULTISAMPLE : GL_TEXTURE_2D, renderable.getID(0), 0); 
		}
	}
	
	/**
	 * resizes all textures of the Framebuffer
	 * 
	 * @param sizeX
	 * @param sizeY
	 */
	public void reSize(int sizeX, int sizeY) {
		if(sizeX == this.sx && sizeY == this.sy)
			return;
		this.depthStencil.reSize(sizeX, sizeY);
		for(FBOTexture t : tex) {
			t.reSize(sizeX, sizeY);
		}
		this.sx = sizeX;
		this.sy = sizeY;
	}
	
	
	public static void setViewPortSize(float f, float g) {
		GL11.glViewport(0, 0, (int)f,(int)g);
	}
	public static void setViewPortSize(float f, float g, float c, float m) {
		GL11.glViewport((int)f, (int)g, (int)c,(int)m);
	}
	public void scaleViewPortSizeDown(float f, float g) {
		setViewPortSize(sx/f, sy/g);
	}
	public void scaleViewPortSizeUp(float f, float g) {
		setViewPortSize(sx*f, sy*g);
	}
	public void resetViewPortSize() {
		GL11.glViewport(0, 0, sx, sy);
	}
	/**
	 * Binds the Framebuffer, resets its Viewportscaling, and clears it.
	 */
	public FrameBuffer clear() {
		this.bind();
		this.resetViewPortSize();
		if(multisample)
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL33.GL_MULTISAMPLE_BIT);
		else
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		return this;
	}
	public FrameBuffer clearBuffers() {
		this.bind();
		if(multisample)
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL33.GL_MULTISAMPLE_BIT);
		else
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		return this;
	}
	public FrameBuffer clearDepth() {
		this.bind();
		this.resetViewPortSize();
		glClear(GL_DEPTH_BUFFER_BIT);
		return this;
	}
	
	/**
	 * Binds the Framebuffer. If bound, everything will be drawn to the Framebuffer�s textures.
	 */
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, id); 
		if(renderer != null)
			renderer.CURRENT_FRAMEBUFFER = this;
	}
	/**
	 * copies a part of the Framebuffer to itself, effectively resizing it. not recommended
	 * @param fx
	 * @param fy
	 * @param smx
	 * @param smy
	 */
	public void blit(int fx, int fy, int smx, int smy) {
		this.bind();
		glBlitFramebuffer(0, 0, fx, fy,
                0, 0, smx, smy,
                GL_COLOR_BUFFER_BIT, GL_LINEAR);
	}
	/**
	 * upscales the content of the Framebuffer to the new scaling. not recommended
	 * @param fx
	 * @param fy
	 * @param smx
	 * @param smy
	 */
	public void blitcS(float scx, float scy) {
		this.bind();
		glBlitFramebuffer(0, 0, sx, sy,
                0, 0, (int)(sx*scx), (int)(sy*scy),
                GL_COLOR_BUFFER_BIT, GL_LINEAR);
	}
	/**
	 * downscales the content of the Framebuffer to the new scaling. not recommended
	 * @param fx
	 * @param fy
	 * @param smx
	 * @param smy
	 */
	public void blitcSD(float scx, float scy) {
		this.bind();
		glBlitFramebuffer(0, 0, sx, sy,
                0, 0, (int)(sx/scx), (int)(sy/scy),
                GL_COLOR_BUFFER_BIT, GL_LINEAR);
	}
	
	/**
	 * Copies data from the Source FBO to this one
	 * @param source
	 * @param sx
	 * @param sy
	 * @param sx2
	 * @param sy2
	 * @param dx
	 * @param dy
	 * @param dx2
	 * @param dy2
	 */
	public void blitFromFBO(FrameBuffer source, int sx, int sy, int sx2, int sy2, int dx, int dy, int dx2, int dy2) {
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, source.getId());
		GL33.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
		GL33.glBlitFramebuffer(sx, sy, sx2, sy2, dx, dy, dx2, dy2, GL_COLOR_BUFFER_BIT, GL_NEAREST); 
	}
	
	/**
	 * Copies data from this FBO to the destination one.<br>
	 * dest may be null, in which case the default Framebuffer is used.
	 * @param source
	 * @param sx
	 * @param sy
	 * @param sx2
	 * @param sy2
	 * @param dx
	 * @param dy
	 * @param dx2
	 * @param dy2
	 */
	public void blitToFBO(FrameBuffer dest, int sx, int sy, int sx2, int sy2, int dx, int dy, int dx2, int dy2) {
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, dest != null ? dest.getId() : 0);
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, id);
		GL33.glBlitFramebuffer(sx, sy, sx2, sy2, dx, dy, dx2, dy2, GL_COLOR_BUFFER_BIT, GL_NEAREST); 
	}
	/**
	 * Binds the default Framebuffer and resets the Viewportscaling
	 */
	public void bindDefaultFrameBuffer() {
		if(renderer != null)
			setViewPortSize(renderer.getWidth(), renderer.getHeight());
		glBindFramebuffer(GL_FRAMEBUFFER, 0); 
	}
	/**
	 * @return the ID of the Framebuffer
	 */
	public int getId() {
		return id;
	}
	/**
	 * Frees all ressources held by the Framebuffer, including the Textures
	 */
	public void free() {
		glDeleteFramebuffers(id);
		for(FBOTexture t : tex)
			t.free();
		depthStencil.free();
	}
	/**
	 * returns the first and default Texture of this framebuffer
	 * @return
	 */
	public FBOTexture getTexture() {
		if(tex.length > 0)
			return tex[0];
		else
			return depthStencil;
	}
	/**
	 * returns all Textures. Depth/Stencil is the last one
	 * @return
	 */
	public FBOTexture[] getAllTextures() {
		return depthStencilAll;
	}
	/**
	 * returns all Textures, excluding Depth/Stencil
	 * @return
	 */
	public FBOTexture[] getTextures() {
		return tex;
	}
	
	/**
	 * returns the num. Texture of this Framebuffer, or default if Texture is nonexistent
	 * @param num
	 * @return
	 */
	public FBOTexture getTexture(int num) {
		if(num < tex.length || num > -1)
			return tex[num];
		else
			return tex[0];
		
	}
	/**
	 * returns the depth-stencil Texture of this Framebuffer
	 * @return
	 */
	public FBOTexture getDepthStencilTexture() {
		return depthStencil;
	}
	
	public int getWidth() {
		return sx;
	}
	
	public int getHeight() {
		return sy;
	}
	
	public void setAutoResize(boolean autoR) {
		this.autoR = autoR;
	}
	
	public boolean isAutoResize() {
		return autoR;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void setMss(float mss) {
		this.mss = mss;
	}
}
