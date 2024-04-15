package Kartoffel.Licht.Engine;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Geo.LibBulletJme.Physics;
import Kartoffel.Licht.Input.KeyInputCallback;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Media.Audio;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.Debug;
import Kartoffel.Licht.Rendering.FrameBuffer;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.BasicShader2D;
import Kartoffel.Licht.Rendering.Shaders.BasicShader3D;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Shaders.SkyboxShader;
import Kartoffel.Licht.Rendering.Shaders.Objects.Light;
import Kartoffel.Licht.Rendering.Shapes.SBox3D;
import Kartoffel.Licht.Rendering.Texture.CubeMap;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.FileDialog;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;

/**
 * A simple interface for creating small applications.<br>
 * For debugging and rendering with openGL.<br>
 * 
 * <code>
 * init();<br>
 * [ //Main Loop<br>
 * ..MAX_TPS->tick();<br>
 * ..MAX_FPS-><br>
 * ..{<br>
 * ....bindMainFrameBuffer();<br>
 * ....paint();<br>
 * ....#draw entities#;<br>
 * ....preProcess();<br>
 * ....#sky rendering#;<br>
 * ....postPaint();<br>
 * ....postProcess();<br>
 * ....paintSprites();<br>
 * ..}<br>
 * ]<br>
 * end();<br>
 * </code>
 *
 */
public abstract class JApp {
	
	
	protected GraphicWindow window = null;
	
	protected final List<GEntity> entities = new ArrayList<>();
	protected final List<Light> lights = new ArrayList<>();
	protected final Vector3f LightColor = new Vector3f(1);
	protected final Vector3f LightDir = new Vector3f(0, -1, 0);
	protected final Vector3f Up = new Vector3f(0, 1, 0);
	protected BasicShader3D shader;
	protected BasicShader2D shader2D;
	protected final Camera camera = new Camera();
	
	protected FrameBuffer framebuffer = null;
	protected PostShaderPack psp;
	
	protected float MAX_FPS = 128;
	protected float MAX_TPS = 128;
	
	protected SkyboxShader shader_sky;
	protected Model skybox_m;
	protected CubeMap skybox_t;
	
	protected long oldTime = Timer.getTime();
	protected long oldTimeF = Timer.getTime();
	public double delta = 0;
	public double deltaF = 0;
	protected Object child = null;
	
	/**
	 * Runs the application
	 * @throws Exception if any exception occurs
	 */
	public void run() throws Exception {
		run(false);
	}
	/**
	 * Runs the application
	 * @param save if the main loop should should catch any Throwable and directly continue with the next cycle (setting this to true is a bad habit)
	 * @throws Exception if any exception occurs
	 */
	public void run(boolean save) throws Exception{
		initi(save);
		Tools.every fps = new Tools.every();
		Tools.every tps = new Tools.every();
		loop(fps, tps, save);
		endt();
	}
	
	protected void endt() throws Exception {
		for(GEntity e : entities)
			if(e != null)
			e.free();
		Physics.free();
		Debug.free();
		FileDialog.free();
		Audio.free();
		end();
		window.free();
	}
	
	protected void initi(boolean save) throws Exception {
		window = new GraphicWindow(true, 0, 0, 1000, 1000, "JApp", 0) {
			@Override
			public void WindowSizeCall(int sx, int sy) {
				camera.getProjectionBox().setAspectRatio((float)sx/sy);
				camera.getProjectionBox().update();
			}
			
			@Override
			public void CursorEnterCall(boolean entered) {
				
			}
			@Override
			public void WindowRefreshCall() {
				try {
					paintAll();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void KeyCall(KeyInputCallback call) {
				if(call.isKeyDown("F11"))
					window.toggleFullscreen();
			}
		};
		Audio.init();
		shader = new BasicShader3D(camera);
		shader.setShading(false);
		shader.setLights(lights);
		shader.setGlobalLightColor(LightColor);
		shader.setGlobalLightDirection(LightDir);
		shader2D = new BasicShader2D();
		shader_sky = new SkyboxShader(camera);
		shader_sky.setSun_dir(LightDir);
		skybox_m = new Model(new SBox3D(1, 1, 1).flipFaces());
		Physics.init(4, false, -50, -50, -50, 50, 50, 50);
		Debug.init(camera);
		child = init();
		
		window.setVisible(true);
	}
	
	protected void loop(Tools.every fps, Tools.every tps, boolean save) {
		while(true)
			try {
				while(!window.WindowShouldClose()) {
					if(Tools.every(1/MAX_TPS, tps)) {
						delta = (Timer.getTime()-oldTime)/1000000000.0;
						oldTime = Timer.getTime();
						if(!tick(delta))
							break;
						Physics.update((float) delta);
					}
					if(Tools.every(1/MAX_FPS, fps)) {
						deltaF = (Timer.getTime()-oldTimeF)/1000000000.0;
						oldTimeF = Timer.getTime();
						
						GraphicWindow.doPollEvents();
						camera.update();
						paintAll();
						window.updateWindow();
						clearBuffers();
						
						Audio.setPosition(camera.getPosition());
						Audio.setOrientation(camera.getOutUp(), camera.getOutDirection());
						FileDialog.update();
					}
					long sleepDur = Math.min(fps.getRemaining(), tps.getRemaining());
					Thread.sleep(sleepDur/1000000, (int) (sleepDur%1000000));
				}
				break;
			} catch (Throwable e1) {
				e1.printStackTrace();
				if(!save)
					break;
			}
	}
	
	/**
	 * Adds a GEntity to the scene. Effectively the same as 'entities.add(GEntity g)', with the change that the return value is the given GEntity. only for convenience
	 * @param g GEntity to be added
	 * @return the given GEntity
	 */
	public GEntity add(GEntity g) {
		entities.add(g);
		return g;
	}
	
	/**
	 * Renders a logo onto the screen using intermediate rendering
	 * @param image BufferedImage containing the logo
	 */
	final protected void renderLogo(Kartoffel.Licht.Java.BufferedImage image) {
		float tileWidth = 1f/image.getWidth()*2;
		float tileHeight = 1f/image.getHeight()*2;
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				GL33.glBegin(GL33.GL_QUAD_STRIP);
				Color c = new Color(image.getRGB(x, y));
				GL33.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, 1);
				GL33.glVertex3f((float)x/image.getWidth()*2-1, 				(float)y/image.getHeight()*2-1, 0);
				GL33.glVertex3f((float)x/image.getWidth()*2-1+tileWidth, 	(float)y/image.getHeight()*2-1, 0);
				GL33.glVertex3f((float)x/image.getWidth()*2-1, 				(float)y/image.getHeight()*2-1+tileHeight, 0);
				GL33.glVertex3f((float)x/image.getWidth()*2-1+tileWidth,	(float)y/image.getHeight()*2-1+tileHeight, 0);
				GL33.glEnd();
			}
		}
	}
	/**
	 * Frees the old skybox (if not null) and sets the new one.
	 * @param path
	 * @param fileType
	 */
	final protected void skybox(String path, String fileType) {
		if(skybox_t != null)
			skybox_t.free();
		if(path == null)
			skybox_t = new CubeMap();
		else
			skybox_t = new CubeMap(path, fileType);
	}
	/**
	 * Clears the GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT and GL_STENCIL_BUFFER_BIT Buffer
	 */
	protected void clearBuffers() {
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT | GL33.GL_STENCIL_BUFFER_BIT);
	}
	/**
	 * Initializes the application.
	 * @return the child of the JAPP (used for reflection)
	 * @throws Exception if any Exception occurs
	 */
	public Object init() throws Exception {
		InputStream is = FileLoader.getFileD("default/textures/icons/icon.png"); if(is != null)
		renderLogo(ImageIO.read(is, 3));
		window.setVisible(true);
		window.updateWindow();
		window.setIcon(ImageIO.read(FileLoader.getFileD("default/textures/icons/icon.png"), 3));
		return null;
	};
	/**
	 * Draws the scene.
	 * @throws Exception
	 */
	final public void paintAll() throws Exception {
		bindMainBuffer();
		paint(deltaF);
		TimePieChart.measure();
		for(GEntity e : entities) {
			if(e != null) {
				Shader s = (Shader) e.getProperty("DedicatedShader");
				if(s == null)
					shader.render(e);
				else
					s.render(e);
			}
		}
		TimePieChart.stamp("Entities");
		preProcess();
		shader_sky.render(skybox_m, skybox_t);
		postPaint(deltaF);
		postProcess();
		paintSprites(deltaF);
	}
	
	/**
	 * Draws the scene. Sky is drawn after 'paint()'
	 * @param buffer the FrameBuffer to draw to
	 * @param cam the Camera to use
	 * @param pp if Post Processing should be enabled. If disabled, all entities will be directly drawn to the buffer
	 * @param sprites if sprites (i.e. Gui..) should be drawn
	 * @param onlyEntities if the method 'paint' should not be invoked
	 * @throws Exception if any exception occurs
	 */
	final public void paintAll(FrameBuffer buffer, Camera cam, boolean pp, boolean sprites, boolean onlyEntities) throws Exception {
		FrameBuffer old_f = framebuffer;
		Camera old_c = new Camera(camera);
		framebuffer = buffer;
		camera.set(cam);
		if(pp)
			bindMainBuffer();
		else
			buffer.clear();
		if(!onlyEntities)
			paint(deltaF);
		shader_sky.render(skybox_m, skybox_t);
		TimePieChart.measure();
		for(GEntity e : entities) {
			if(e != null) {
				Shader s = (Shader) e.getProperty("DedicatedShader");
				if(s == null)
					shader.render(e);
				else
					s.render(e);
			}
		}
		TimePieChart.stamp("Entities " + buffer.getName());
		preProcess();
		postPaint(deltaF);
		if(pp)
			postProcess();
		if(sprites)
			paintSprites(deltaF);
		framebuffer = old_f;
		camera.set(old_c);
	}
	/**
	 * Draws the scene.
	 * @param buffer the FrameBuffer to draw to
	 * @param cam the Camera to use
	 * @param draw a runnable in which everything to draw should be drawn.
	 * @throws Exception if any exception occurs
	 */
	final public void helpPaint(FrameBuffer buffer, Camera cam, Runnable draw) throws Exception {
		FrameBuffer old_f = framebuffer;
		Camera old_c = new Camera(camera);
		framebuffer = buffer;
		camera.set(cam);
		bindMainBuffer();
		draw.run();
		framebuffer = old_f;
		camera.set(old_c);
	}
	/**
	 * Paints all sprites (i.e Gui..)
	 * @param delta variable to define the time passed in seconds: <blockquote>secondsPassed += 1*delta</blockquote>
	 */
	protected void paintSprites(double delta) {}
	/**
	 * Does post processing
	 */
	protected void postProcess() {};
	/**
	 * Does pre-post processing
	 */
	protected void preProcess() {};
	/**
	 * Binds the Frame Buffer all geometry should be rendered to
	 */
	protected void bindMainBuffer() {
		if(framebuffer == null) {
			GL33.glViewport(0, 0, window.getWidth(), window.getHeight());
			GL33.glBindFramebuffer(GL_FRAMEBUFFER, 0); 
		}
		else {
			framebuffer.clear();
		}
	};
	/**
	 * Used for e.g. shadow mapping.
	 * @param delta variable to define the time passed in seconds: <blockquote>secondsPassed += 1*delta</blockquote>
	 * @throws Exception if any Exception occurs
	 */
	public void paint(double delta) throws Exception {};
	/**
	 * Draws more geometry (e.g. transparent Objects)
	 * @param delta variable to define the time passed in seconds: <blockquote>secondsPassed += 1*delta</blockquote>
	 * @throws Exception if any Exception occurs
	 */
	public void postPaint(double delta) throws Exception {};
	/**
	 * Ticks the whole application. (e.g. Camera movement)
	 * @param delta variable to define the time passed in seconds: <blockquote>secondsPassed += 1*delta</blockquote>
	 * @return if the application should close
	 * @throws Exception if any Exception occurs
	 */
	public abstract boolean tick(double delta) throws Exception;
	/**
	 * Ends the application. (e.g. free network connections/models/..)
	 * @throws Exception if any Exception occurs
	 */
	public void end() throws Exception {}
	
	public GraphicWindow getWindow() {
		return window;
	}

	public void setWindow(GraphicWindow window) {
		this.window = window;
	}

	public BasicShader3D getShader() {
		return shader;
	}

	public void setShader(BasicShader3D shader) {
		this.shader = shader;
	}

	public BasicShader2D getShader2D() {
		return shader2D;
	}

	public void setShader2D(BasicShader2D shader2d) {
		shader2D = shader2d;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera.set(camera);
	}

	public SkyboxShader getShader_sky() {
		return shader_sky;
	}

	public void setShader_sky(SkyboxShader shader_sky) {
		this.shader_sky = shader_sky;
	}

	public Model getSkybox_m() {
		return skybox_m;
	}

	public void setSkybox_m(Model skybox_m) {
		this.skybox_m = skybox_m;
	}

	public CubeMap getSkybox_t() {
		return skybox_t;
	}

	public void setSkybox_t(CubeMap skybox_t) {
		this.skybox_t = skybox_t;
	}

	public long getOldTime() {
		return oldTime;
	}

	public void setOldTime(long oldTime) {
		this.oldTime = oldTime;
	}

	public double getDelta() {
		return delta;
	}
	public double getDeltaF() {
		return deltaF;
	}
	public double getTPS() {
		return 1/delta;
	}
	public double getFPS() {
		return 1/deltaF;
	}

	public Object getChild() {
		return child;
	}

	public void setChild(Object child) {
		this.child = child;
	}

	public List<GEntity> getEntities() {
		return entities;
	}

	public List<Light> getLights() {
		return lights;
	}

	public Vector3f getLightColor() {
		return LightColor;
	}

	public Vector3f getLightDir() {
		return LightDir;
	}

	public Vector3f getUp() {
		return Up;
	};
	
	public FrameBuffer getFramebuffer() {
		return framebuffer;
	}
	
	public void setFramebuffer(FrameBuffer framebuffer) {
		this.framebuffer = framebuffer;
	}
	

}
