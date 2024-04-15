package Kartoffel.Licht.Rendering;


import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;

import Kartoffel.Licht.Geo.AABB;
import Kartoffel.Licht.Geo.JPhysics;
import Kartoffel.Licht.Geo.Ray;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Shapes.SLine;
import Kartoffel.Licht.Rendering.Shapes.SLineBox;
import Kartoffel.Licht.Rendering.Shapes.SPBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Tools;

public class Debug {
	
	private static boolean init = false;
	
	public static Camera camera;
	public static Model m;
	public static Model cm;
	public static Shader shader;
	public static Color COLOR = Color.RED;
	public static boolean INVERT_COLORS = false;
	public static int FILL_MODE = Model.GL_FILLMODE_LINE;
	public static boolean PERSPECTIVE = true;
	public static float ZMUL = 0.1f;
	
	public static void init(Camera camera) {
		if(init)
			return;
		Debug.camera = camera;
		m = new Model(new SLine(0, 0, 0, 1, 1, 1));
		cm = new Model(new SLineBox());
		shader = new Shader(FileLoader.readFileD("default/shaders/debug.vert"), FileLoader.readFileD("default/shaders/debug.frag"), null);
		init = true;
	}
	public static void drawModel(Model mod, Renderable texture, Matrix4f vm, Matrix4f pm, Matrix4f tm, float xd, float yd, float zd) {
		if(camera == null)
			throw new RuntimeException("Please init using �Debug.init(Camera camera)�");
		if(mod == null)
			return;
		int ds = mod.getFILL_MODE();
		mod.setFILL_MODE(FILL_MODE);
		camera.update();
		shader.bind();
		shader.setUniformVec3("color", COLOR.getRed()/255f, COLOR.getGreen()/255f, COLOR.getBlue()/255f);
		shader.setUniformMatrix4f("viewMat", vm);
		shader.setUniformMatrix4f("projectionMat", pm);
		shader.setUniformMatrix4f("transmat", tm == null ? Tools.IDENTITY_MATRIX : tm);
		shader.setUniformVec3("shaded", xd, yd, zd);
		shader.setUniformInt("materialCount", mod.getMaterial_count());
		shader.setUniformBool("textured", texture != null);
		shader.setUniformBool("model", false);
		shader.setUniformBool("invert", INVERT_COLORS);
		shader.setUniformFloat("zmul", ZMUL);
		shader.render(mod, texture);
		mod.setFILL_MODE(ds);
	}
	public static void drawCamera(Camera cam) {
		if(camera == null)
			throw new RuntimeException("Please init using �Debug.init(Camera camera)�");
		if(cam == null)
			return;
		camera.update();
		shader.bind();
		shader.setUniformVec3("color", COLOR.getRed()/255f, COLOR.getGreen()/255f, COLOR.getBlue()/255f);
		shader.setUniformMatrix4f("viewMat", PERSPECTIVE ? camera.getViewMatrix() : Tools.IDENTITY_MATRIX);
		shader.setUniformBool("textured", false);
		shader.setUniformBool("model", false);
		shader.setUniformVec3("shaded", 0, 0, 0);
		shader.setUniformMatrix4f("projectionMat", PERSPECTIVE ? camera.getProjection() : Tools.IDENTITY_MATRIX);
		shader.setUniformMatrix4f("transmat", cam.getProViewMatrix().invert(new Matrix4f()));
		shader.setUniformBool("invert", INVERT_COLORS);
		shader.setUniformFloat("zmul", ZMUL);
		shader.render(cm, null);
	}
	public static void drawLine(Ray ray) {
		 drawLine((float)ray.x, (float)ray.y, (float)ray.z, (float)(ray.x+ray.dx), (float)(ray.y+ray.dy), (float)(ray.z+ray.dz), Tools.IDENTITY_MATRIX);
	}
	public static void drawLine(Vector3f a, Vector3f b) {
		 drawLine((float)a.x, (float)a.y, (float)a.z, (float)(b.x), (float)(b.y), (float)(b.z), Tools.IDENTITY_MATRIX);
	}
	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		 drawLine((float)x1, (float)y1, (float)z1, (float)x2, (float)y2, (float)z2, Tools.IDENTITY_MATRIX);
	}
	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, Matrix4f trans) {
		 drawLine((float)x1, (float)y1, (float)z1, (float)x2, (float)y2, (float)z2, trans);
	}
	public static void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, Matrix4f trans) {
		if(camera == null)
			throw new RuntimeException("Please init using �Debug.init(Camera camera)�");
		camera.update();
		shader.bind();
		shader.setUniformVec3("pos1", (float)x1, (float)y1, (float)z1);
		shader.setUniformVec3("pos2", (float)(x2), (float)(y2), (float)(z2));
		shader.setUniformVec3("color", COLOR.getRed()/255f, COLOR.getGreen()/255f, COLOR.getBlue()/255f);
		shader.setUniformMatrix4f("viewMat", PERSPECTIVE ? camera.getViewMatrix() : Tools.IDENTITY_MATRIX);
		shader.setUniformBool("textured", false);
		shader.setUniformBool("model", true);
		shader.setUniformVec3("shaded", 0, 0, 0);
		shader.setUniformMatrix4f("projectionMat", PERSPECTIVE ? camera.getProjection() : Tools.IDENTITY_MATRIX);
		shader.setUniformMatrix4f("transmat", trans);
		shader.setUniformBool("invert", INVERT_COLORS);
		shader.render(m, null);
	}
	public static void drawPoint(double x, double y, double z) {
		 drawLine(x, y, z, x, y+0.1f, z);
	}
	public static void drawPoint(float x, float y, float z) {
		 drawLine(x, y, z,  x, y+0.1f, z);
	}
	public static void drawPoint(Vector3f v) {
		 drawPoint(v.x, v.y, v.z);
	}
	public static void drawAABB(AABB aabb) {
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x2, aabb.y1, aabb.z1, Tools.IDENTITY_MATRIX);
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x1, aabb.y2, aabb.z1, Tools.IDENTITY_MATRIX);
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x1, aabb.y1, aabb.z2, Tools.IDENTITY_MATRIX);
		
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x1, aabb.y2, aabb.z2, Tools.IDENTITY_MATRIX);
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x2, aabb.y1, aabb.z2, Tools.IDENTITY_MATRIX);
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x2, aabb.y2, aabb.z1, Tools.IDENTITY_MATRIX);
		
	}
	public static void drawAABB(AABB aabb, Matrix4f trans) {
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x2, aabb.y1, aabb.z1, trans);
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x1, aabb.y2, aabb.z1, trans);
		drawLine(aabb.x1, aabb.y1, aabb.z1, aabb.x1, aabb.y1, aabb.z2, trans);
		
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x1, aabb.y2, aabb.z2, trans);
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x2, aabb.y1, aabb.z2, trans);
		drawLine(aabb.x2, aabb.y2, aabb.z2, aabb.x2, aabb.y2, aabb.z1, trans);
		
	}
	
	private static float percent = 0;
	private static int selected = -1;
	private static float traveled = 0;
	private static final int MAX_TRAVEL = 200;
	private static final int MOUSE_BUTTON = 0;
	private static final String SWITCH_KEY = "M";
	public static final int DT_ROTATION_AS_DIRECTION = 1; 
	public static final int DT_NOCAPTURE = 2; 
	public static final int DT_ROTATION = 3; 
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	private static int modus = 0;
	/**
	 * Messy. Rotation broken.
	 */
	public static boolean drawTransform(Matrix4f trans, Vector3f pos, Vector3f rot, Vector3f scale, Camera cam, GraphicWindow window, int... flags) {
		GL33.glLineWidth(10);
		boolean direction = Tools.containsFlag(flags, DT_ROTATION_AS_DIRECTION);
		boolean rotation = Tools.containsFlag(flags, DT_ROTATION);
		float mouseX = (float)window.getCallback_Cursor().getX()/window.getWidth()*2-1;
		float mouseY = (float)window.getCallback_Cursor().getY()/window.getHeight()*2-1;
		Matrix4f mat;
		if(trans != null) {
			mat = new Matrix4f(trans);
			mat.normalize3x3();
		}else {
			mat = new Matrix4f().identity();
			if(pos != null)
				mat.translate(pos);
			if(rot != null) {
				if(direction && rotation)
					mat.lookAlong(rot.x, rot.y, rot.z, UP.x, UP.y, UP.z);
				else {
					mat.rotateX((float) Math.toRadians(rot.x));
					mat.rotateY((float) Math.toRadians(rot.y));
					mat.rotateZ((float) Math.toRadians(rot.z));
				}
			}
		}
		Vector4f origin = new Vector4f(0, 0, 0, 1);
		mat.transform(origin);
		
		if(modus == 0) {
			if(rotation) {
				COLOR = Color.MAGENTA;
				if(drawCircle(mat, 50, 0, mouseX, mouseY, cam) && selected == -1) {
					percent = rot.x;
					selected = 3;
				}
				COLOR = Color.YELLOW;
				if(drawCircle(mat, 50, 1, mouseX, mouseY, cam) && selected == -1) {
					percent = rot.y;
					selected = 4;
				}
				COLOR = Color.CYAN;
				if(drawCircle(mat, 50, 2, mouseX, mouseY, cam) && selected == -1) {
					percent = rot.z;
					selected = 5;
				}
			}
		}
//		Vector4f w = new Vector4f(mat.m10(), mat.m11(), mat.m12(), 1);
//		w.add(mat.m30(), mat.m31(), mat.m32(), 0);
//		COLOR = Color.GRAY;
//		drawLine(w, origin);
		
		//No more rotation
		if(modus == 0)
			mat.setRotationXYZ(0, 0, 0);
		
		Vector4f x = new Vector4f(1, 0, 0, 1);
		Vector4f y = new Vector4f(0, 1, 0, 1);
		Vector4f z = new Vector4f(0, 0, 1, 1);
//		x.mul(mat);
//		y.mul(mat);
//		z.mul(mat);
		Vector3f transl = mat.getTranslation(new Vector3f());
		x.x += transl.x;
		x.y += transl.y;
		x.z += transl.z;
		y.x += transl.x;
		y.y += transl.y;
		y.z += transl.z;
		z.x += transl.x;
		z.y += transl.y;
		z.z += transl.z;
		
		COLOR = modus == 0 ? Color.RED : Color.RED.darker();
		drawLine(origin, x);
		COLOR = modus == 0 ? Color.GREEN : Color.GREEN.darker();
		drawLine(origin, y);
		COLOR = modus == 0 ? Color.BLUE : Color.BLUE.darker();
		drawLine(origin, z);
		
		if(window.getCallback_Key().isKeyPressed(SWITCH_KEY))
			modus = modus == 0 ? scale != null ? 1 : 0 : 0;
		
		//Drag-test
		boolean mbp = window.getCallback_Key().isMouseButtonPressed(MOUSE_BUTTON);
		if(window.getCallback_Key().isMouseButtonDown(MOUSE_BUTTON) && !Tools.containsFlag(flags, DT_NOCAPTURE)) {
			x.mul(cam.getProViewMatrix());
			y.mul(cam.getProViewMatrix());
			z.mul(cam.getProViewMatrix());
			origin.mul(cam.getProViewMatrix());
			x.div(x.w);
			y.div(y.w);
			z.div(z.w);
			origin.div(origin.w);
			
			if(selected == -1 && mbp) {
				if(JPhysics.intersectionLinePoint(origin.x, origin.y, x.x, x.y, mouseX, -mouseY, 0.01f)) {
					percent = getPPLS(mouseX, -mouseY, origin.x, origin.y, x.x, x.y).z;
					selected = 0;
					traveled = 0;
				}
				if(JPhysics.intersectionLinePoint(origin.x, origin.y, y.x, y.y, mouseX, -mouseY, 0.01f)) {
					percent = getPPLS(mouseX, -mouseY, origin.x, origin.y, y.x, y.y).z;
					selected = 1;
					traveled = 0;
				}
				if(JPhysics.intersectionLinePoint(origin.x, origin.y, z.x, z.y, mouseX, -mouseY, 0.01f)) {
					percent = getPPLS(mouseX, -mouseY, origin.x, origin.y, z.x, z.y).z;
					selected = 2;
					traveled = 0;
				}
			}
			else {
				if(selected == 0) {
					Vector4f pp = getPPLS(mouseX, -mouseY, origin.x, origin.y, x.x, x.y);
					if(modus == 0)
						pos.x -= pp.z-percent;
					else if(modus == 1) {
						scale.x -= pp.z-percent;
						percent = pp.z;
					}
					traveled -= pp.z-percent;
				}
				if(selected == 1) {
					Vector4f pp = getPPLS(mouseX, -mouseY, origin.x, origin.y, y.x, y.y);
					if(modus == 0)
						pos.y -= pp.z-percent;
					else if(modus == 1) {
						scale.y -= pp.z-percent;
						percent = pp.z;
					}
					traveled -= pp.z-percent;
				}
				if(selected == 2) {
					Vector4f pp = getPPLS(mouseX, -mouseY, origin.x, origin.y, z.x, z.y);
					if(modus == 0)
						pos.z -= pp.z-percent;
					else if(modus == 1) {
						scale.z -= pp.z-percent;
						percent = pp.z;
					}
					traveled -= pp.z-percent;
				}
					if(selected == 3) {
						if(direction) {
							mat.rotateX((float) Math.atan2(mouseX-origin.x, -mouseY-origin.y));
							rot.set(mat.m10(), mat.m11(), mat.m12());
						}
						else
							rot.x = (float) (percent-Math.toDegrees(Math.atan2(mouseX-origin.x, -mouseY-origin.y)));
					}
					if(selected == 4) {
						if(direction) {
							mat.rotateY((float) Math.atan2(mouseX-origin.x, -mouseY-origin.y));
							rot.set(mat.m00(), mat.m01(), mat.m02());
						}
						else
							rot.y = (float) (percent+Math.toDegrees(Math.atan2(mouseX-origin.x, -mouseY-origin.y)));
						}
				if(selected == 5) {
					if(direction) {
						mat.rotateZ((float) Math.atan2(mouseX-origin.x, -mouseY-origin.y));
						rot.set(mat.m10(), mat.m11(), mat.m12());
					}
					else
						rot.z = (float) (percent-Math.toDegrees(Math.atan2(mouseX-origin.x, -mouseY-origin.y)));
				}
				if(Math.abs(traveled) > MAX_TRAVEL)
					selected = -1;
			}
		}
		else
			selected = -1;
		return selected != -1;
		
	}
//	private static Vector4f hit(Vector4f p, Camera cam) {
//		Vector4f point = p.mul(cam.getProViewMatrix(), new Vector4f());
//		float w = point.w;
//		point.div(w);
//		point.mul(w);
//		point.mul(cam.getProViewMatrix().invert(new Matrix4f()));
//		return point.sub(p);
//	}
	private static Vector4f getPPLS(float x0, float y0, float x1, float y1, float x2, float y2) {
		float m = (y1-y2)/(x1-x2);
		float b = y1/2+y2/2-(x1/2+x2/2)*m;
		float h = (x0-(y0-b)*-m)/(m*m+1);
		Vector4f point = new Vector4f();
		point.x = h;
		point.y = (h*m+b);
		float percent = -((point.x-x2)+(point.y-y2))/((x2-x1)+(y2-y1));
		point.z = percent;
		//Debug
//		PERSPECTIVE = false;
//		drawLine(pp.x, pp.y, 0, x0, y0, 0);
//		
//		PERSPECTIVE = true;
		return point;
	}
//	private static float dot(Vector2f a, Vector2f b) {
//		return a.x*b.x+a.y*b.y;
//	}
	private static boolean drawCircle(Matrix4f mat, int amount, int orientation, float mouseX, float mouseY, Camera cam) {
		Vector4f bef = null;
		boolean click = false;
		for(int i = 0; i <= amount; i++) {
			Vector4f v = null;
			if(orientation == 0)
				v = new Vector4f((float)Math.cos(Math.PI*i/amount*2), 0, (float)Math.sin(Math.PI*i/amount*2), 1);
			if(orientation == 1)
				v = new Vector4f((float)Math.cos(Math.PI*i/amount*2), (float)Math.sin(Math.PI*i/amount*2), 0, 1);
			if(orientation == 2)
				v = new Vector4f(0, (float)Math.cos(Math.PI*i/amount*2), (float)Math.sin(Math.PI*i/amount*2), 1);
			v.mul(mat);
			if(bef != null) {
				drawLine(v, bef);
				Vector4f p1 = new Vector4f(v.x, v.y, v.z, 1);
				Vector4f p2 = new Vector4f(bef.x, bef.y, bef.z, 1);
				p1.mul(cam.getProViewMatrix());
				p2.mul(cam.getProViewMatrix());
				p1.div(p1.w);
				p2.div(p2.w);
				if(JPhysics.intersectionLinePoint(p1.x, p1.y, p2.x, p2.y, mouseX, -mouseY, 0.01f))
					click = true;
			}
			bef = v;
		}
		return click;
	}
	private static void drawLine(Vector4f a, Vector4f b) {
		 drawLine((float)a.x, (float)a.y, (float)a.z, (float)(b.x), (float)(b.y), (float)(b.z));
	}
	
	public static void free() {
		m.free();
		shader.free();
	}
	
	public static void displayImage(BufferedImage image) {
		GLCapabilities prev = null;
		try {
			prev = GL.getCapabilities();
		} catch (Exception e) {}
		GL.setCapabilities(null);
		GraphicWindow gw = new GraphicWindow("Image " + image.toString());
		gw.setMMSize(50, 50, 2000, 2000);
		gw.setWidth(image.getWidth());
		gw.setHeight(image.getHeight());
		gw.setVisible(true);
		GLFW.glfwMakeContextCurrent(0);
		GLCapabilities cap = GL.getCapabilities();
		GL.setCapabilities(null);
		new Thread() {
			public static float GRID_SIZE = 20;
			public void run() {
				GL.setCapabilities(cap);
				gw.bindGLFW();
				Texture t = new Texture(image);
				Model m = new Model(new SPBox2D(1f, 1f));
				Shader shader = new Shader("#version 330 core\n"
						+ "in vec3 vertices;"
						+ "in vec2 textures;"
						+ "out vec2 tex_coords;"
						+ "void main() {tex_coords = textures;	gl_Position = vec4(vertices, 1);}", "#version 330 core\nlayout (location = 0) out vec4 FragColor;uniform sampler2D sampler;uniform vec2 winSize;uniform vec2 texSize;"
						+ "in vec2 tex_coords;\nvoid main() {\n"
						+ "FragColor = texture2D(sampler, tex_coords*texSize);\nfloat a = mod(floor(tex_coords.x*winSize.x)+floor(tex_coords.y*winSize.y), 2);FragColor.rgb = mix(FragColor.rgb, a == 0 ? vec3(1) : vec3(0), 1-(FragColor.a == 0 ? 1 : FragColor.a));FragColor.a=1;}", "transparentShader");
				int width = 0, height = 0;
				while(!gw.WindowShouldClose()) {
					shader.bind();
					shader.setUniformVec2("winSize", gw.getWidth()/GRID_SIZE, gw.getHeight()/GRID_SIZE);
					shader.setUniformVec2("texSize", (float)gw.getWidth()/image.getWidth(), (float)gw.getHeight()/image.getHeight());
					shader.render(m, t);
					gw.updateWindow(true);
					if(gw.getCallback_Key().isKeyPressed("F11")) //TODO wont work
						gw.toggleFullscreen();
					if(gw.getWidth() != width || gw.getHeight() != height) {
						gw.adjustViewport();
						width = gw.getWidth();
						height = gw.getHeight();
					}
				}
				t.free();
				m.free();
				gw.free();
			};
			
		}.start();
		GL.setCapabilities(prev);
	}

}
