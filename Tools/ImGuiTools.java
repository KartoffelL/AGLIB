package Kartoffel.Licht.Tools;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.JavaSystem;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Texture.Material.Material;
import Kartoffel.Licht.Rendering.Texture.Material.MaterialType;
import Kartoffel.Licht.Rendering.Texture.Material.PBRMaterial;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;

public class ImGuiTools {
	
	private static HashMap<Shader, GLSLEditor> editors = new HashMap<Shader, ImGuiTools.GLSLEditor>();
	
	public static ImGuiImplGlfw g;
	public static ImGuiImplGl3 g2;
	private static int flags = 0;
	private static boolean viewports_noTaskbarIcons = false;
	
	/**
	 * Initializes a static ImGui instance.
	 * @param window
	 */
	static public void ImGui_Init(GraphicWindow window) {
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.setConfigFlags(flags);
		io.setConfigViewportsNoTaskBarIcon(viewports_noTaskbarIcons);
		g = new ImGuiImplGlfw();
		g2 = new ImGuiImplGl3();
		g.init(window.getWINDOW_ID(), true);
		g2.init("#version 330 core");
//		ImGui.getIO().setIniFilename(null); //Disables init file saving
	}
	public static void free() {
		if(g != null)
			g.dispose();
		if(g2 != null)
			g2.dispose();
	}
	/**
	 * Generates an ImGuiImplGlfw
	 * @param window
	 * @return
	 */
	public static ImGuiImplGlfw generateWindowImplementation(GraphicWindow window) {
		ImGuiImplGlfw g = new ImGuiImplGlfw();
		g.init(window.getWINDOW_ID(), true);
		return g;
	}
	/**
	 * Sets flags for the static ImGui instance.
	 * @param docking
	 * @param viewports
	 * @param viewports_noTaskbarIcons
	 * @param nav_keyboard
	 */
	static public void ImGuiFlags(boolean docking, boolean viewports, boolean viewports_noTaskbarIcons, boolean nav_keyboard) {
		if(g != null)
			throw new RuntimeException("ImGui already initialized! Function has to be called before initialization");
		if (docking)
			flags |= ImGuiConfigFlags.DockingEnable;
		if (viewports)
			flags |= ImGuiConfigFlags.ViewportsEnable;
		if (nav_keyboard)
			flags |= ImGuiConfigFlags.NavEnableKeyboard;
		ImGuiTools.viewports_noTaskbarIcons = viewports_noTaskbarIcons;
	}
	/**
	 * Returns true if ImGui requests a capture
	 * @return
	 */
	static public boolean wantsCapture() {
		return ImGui.getIO().getWantCaptureMouse();
	}
	/**
	 * This function has to be run every time something should be drawn with ImGui.<br>
	 * Uses the static ImGui instance
	 */
	static public void ImGui_startDraw() {
		ImGuiCheck();
		ImGui_startDraw(g);
	}
	/**
	 * This function has to be run every time something should be drawn with ImGui.<br>
	 * Uses the given ImGuiImplGlfw
	 * @param ImGuiImplGlfw impl
	 */
	static public void ImGui_startDraw(ImGuiImplGlfw impl) {
		impl.newFrame();
		ImGui.newFrame();
	}
	/**
	 * This function has to be run after everything is finished with ImGui. This function will then draw everything to the screen.<br>
	 * Uses the static ImGui instance
	 */
	static public void ImGui_endDraw() {
		ImGuiCheck();
		ImGui_endDraw(g2);
	}
	
	/**
	 * This function has to be run after everything is finished with ImGui. This function will then draw everything to the screen.<br>
	 * Uses the given ImGuiImplG13
	 */
	static public void ImGui_endDraw(ImGuiImplGl3 gl) {
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		ImGui.render();
		gl.renderDrawData(ImGui.getDrawData());
		
		if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long bwp = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(bwp);
		}
		
		ImGui.endFrame();
		
	}
	
	static private void ImGuiCheck() {
		if(g == null)
			throw new RuntimeException("ImGui not initialized! use 'ImGuiTools.ImGui_Init(GraphicWindow window)' to init!");
	}
	
	/**
	 * Opens draws glsl editors for the given shaders.
	 * @param shader
	 */
	static public void GLSLEditor_Window(Shader... shader) {
		for(Shader s : shader) {
			if(!editors.containsKey(s))
				editors.put(s, new GLSLEditor(s));
		}
		GLSLEditor[] editor = editors.values().toArray(new GLSLEditor[editors.size()]);
		for(GLSLEditor e : editor) {
			boolean c = true;
			for(Shader s : shader)
				if(s == e.shader)
					c = false;
			if(c)
				editors.remove(e.shader);
		}
			
		for(GLSLEditor e : editors.values()) {
			e.GLSLEditor_Window();
		}
	}
	
	static public void GLSLEditor_Window_WL(Shader s) {
		if(!editors.containsKey(s))
			editors.put(s, new GLSLEditor(s));
		editors.get(s).frag();
	}
	
	static class GLSLEditor {
		
		Shader shader;
		//Editor
		TextEditor EDITOR = new TextEditor();
		TextEditor EDITOR2 = new TextEditor();
		TextEditor EDITOR3 = new TextEditor();
		String EDITOR_old = "";
		String EDITOR2_old = "";
		String EDITOR3_old = "";
		long EDITOR_old_timestamp = 0;
		long EDITOR2_old_timestamp = 0;
		long EDITOR3_old_timestamp = 0;
		
		static final int MAX_CHARACTERS = 20000;
		static final int NULLIFY_THA = 5;
		static final int SAVE = 10;
		boolean init = false;
		
		public GLSLEditor(Shader shader) {
			this.shader = shader;
		}
		
		public void frag() {
			EDITOR.render("FRAGMENT");
			//Character limit
	        if(EDITOR.getText().length() > MAX_CHARACTERS)
	        	EDITOR.setText(EDITOR.getText().substring(0, MAX_CHARACTERS-2));
	        
	        //AUTO-VALIDATION
	        if(!EDITOR.getText().equalsIgnoreCase(EDITOR_old))
	        	EDITOR_old_timestamp = 0;
	        EDITOR_old_timestamp++;
	        
	        if(EDITOR_old_timestamp == SAVE) {
	        	shader.setFragmentFile(EDITOR.getText().length() <= NULLIFY_THA ? null : EDITOR.getText());
	        	shader.updateProgram();
	        	String error = shader.getErrors()[1];
	        	HashMap<Integer, String> err = new HashMap<Integer, String>();
	        	if(error != null) {
		        	String[] lines = error.split("\n");
		        	for(String l : lines) {
		        		String[] tokens = l.split(" ");
		        		if(tokens[0].equals("ERROR:")) {
		        			if(tokens[1].startsWith("0:")) {
		        				int line = Integer.parseInt(tokens[1].substring(2, tokens[1].length()-1));
		        				String message = "";
		        				for(int i = 2; i < tokens.length; i++) message += tokens[i]+" ";
		        				err.put(line, message);
		        			}
		        		}
		        	}
	        	}
				EDITOR.setErrorMarkers(err);
	        	Logger.log("Validated Program!", Color.YELLOW);
	        }
	        EDITOR_old = EDITOR.getText();
		
		}
		
		public void vert() {

			EDITOR2.render("VERTEX");
			//Character limit
	        if(EDITOR2.getText().length() > MAX_CHARACTERS)
	        	EDITOR2.setText(EDITOR2.getText().substring(0, MAX_CHARACTERS-2));
	        
	        //AUTO-VALIDATION
	        if(!EDITOR2.getText().equalsIgnoreCase(EDITOR2_old))
	        	EDITOR2_old_timestamp = 0;
	        EDITOR2_old_timestamp++;
	        
	        if(EDITOR2_old_timestamp == SAVE) {
	        	shader.setVertexFile(EDITOR2.getText().length() <= NULLIFY_THA ? null : EDITOR2.getText());
	        	shader.updateProgram();
	        	String error = shader.getErrors()[0];
	        	HashMap<Integer, String> err = new HashMap<Integer, String>();
	        	if(error != null) {
		        	String[] lines = error.split("\n");
		        	for(String l : lines) {
		        		String[] tokens = l.split(" ");
		        		if(tokens[0].equals("ERROR:")) {
		        			if(tokens[1].startsWith("0:")) {
		        				int line = Integer.parseInt(tokens[1].substring(2, tokens[1].length()-1));
		        				String message = "";
		        				for(int i = 2; i < tokens.length; i++) message += tokens[i]+" ";
		        				err.put(line, message);
		        			}
		        		}
		        	}
	        	}
				EDITOR2.setErrorMarkers(err);
	        	Logger.log("Validated Program!", Color.YELLOW);
	        }
	        EDITOR2_old = EDITOR2.getText();
		
		}
		
		public void geo() {

			EDITOR3.render("GEOMETRY");
			//Character limit
	        if(EDITOR3.getText().length() > MAX_CHARACTERS)
	        	EDITOR3.setText(EDITOR3.getText().substring(0, MAX_CHARACTERS-2));
	        
	        //AUTO-VALIDATION
	        if(!EDITOR3.getText().equalsIgnoreCase(EDITOR3_old))
	        	EDITOR3_old_timestamp = 0;
	        EDITOR3_old_timestamp++;
	        
	        if(EDITOR3_old_timestamp == SAVE) {
	        	shader.setGeometryFile(EDITOR3.getText().length() <= NULLIFY_THA ? null : EDITOR3.getText());
	        	shader.updateProgram();
	        	String error = shader.getErrors()[2];
	        	HashMap<Integer, String> err = new HashMap<Integer, String>();
	        	if(error != null) {
		        	String[] lines = error.split("\n");
		        	for(String l : lines) {
		        		String[] tokens = l.split(" ");
		        		if(tokens[0].equals("ERROR:")) {
		        			if(tokens[1].startsWith("0:")) {
		        				int line = Integer.parseInt(tokens[1].substring(2, tokens[1].length()-1));
		        				String message = "";
		        				for(int i = 2; i < tokens.length; i++) message += tokens[i]+" ";
		        				err.put(line, message);
		        			}
		        		}
		        	}
	        	}
				EDITOR3.setErrorMarkers(err);
	        	Logger.log("Validated Program!", Color.YELLOW);
	        }
	        EDITOR3_old = EDITOR3.getText();
		
		}
		
		public void GLSLEditor_Window() {
			if(!init) {
				EDITOR.setText(Tools.null_check(shader.getFragmentFile()));
				EDITOR2.setText(Tools.null_check(shader.getVertexFile()));
				EDITOR3.setText(Tools.null_check(shader.getGeometryFile()));
				EDITOR.setLanguageDefinition(TextEditorLanguageDefinition.glsl());
				EDITOR2.setLanguageDefinition(TextEditorLanguageDefinition.glsl());
				EDITOR.setLanguageDefinition(TextEditorLanguageDefinition.glsl());
				init = true;
			}
			String h = getTrailingBit(shader);
			ImGui.beginTabBar("Shaders "+h);
			if(ImGui.beginTabItem("Fragment Shader"+h)) {
				frag();
				ImGui.endTabItem();
			}
			
			if(ImGui.beginTabItem("Vertex Shader"+h)) {
				vert();
				ImGui.endTabItem();
			}
			if(ImGui.beginTabItem("Geometry Shader"+h)) {
				geo();
				ImGui.endTabItem();
			}
			ImGui.endTabBar();
		}
	}
	
	//Logger
	static ImString input = new ImString();
	static List<String> log = new ArrayList<>();
	/**
	 * Opens an ImGui window containing console in-/output
	 */
	public static void Logger_Window() {
		ImGui.begin("Consol:");
		if(ImGui.inputText("Input", input, ImGuiInputTextFlags.EnterReturnsTrue)) {
			if(input.get().length() > 0) {
				Logger.log(input.get(), Color.CYAN);
				input.clear();
			}
			ImGui.setKeyboardFocusHere(-1);
		}
		ImGui.beginChild("Log");
		int h = 50;
		int index = 0;
		log.clear();
		for(int l = 0; l < h; l++) {
			int ii = Tools.getLog().indexOf("\n", index+1);
			if(ii == -1)
				break;
			String s = Tools.getLog().substring(index+1, ii);
			log.add(s);
			index = ii;
		}
		for(int l = log.size()-1; l > -1; l--) {
			String s = log.get(l);
			int i = s.indexOf("�");
			if(i != -1 && s.length() > i+3) {
				Color color = Logger.fromHex(s.substring(i+1, i+7));
				ImGui.textColored(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), s.substring(7));
			}else {
				ImGui.text(s);
			}
			if(h-- < 0)
				break;
		}
		ImGui.endChild();
		
		ImGui.end();
	
	}
	//Operating System
	static OperatingSystemMXBean  os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	static float[] cpu = new float[50];
	static int m = 0;
	/**
	 * Opens an ImGui window containing information about the OS
	 * @param update length of the histogram
	 */
	public static void Operating_Window(int update) {
		ImGui.begin("Operating System:");
		ImGui.plotHistogram("", cpu, cpu.length, 0, "CPU Time");
		if(m < 0) {
			for(int i = 1; i < cpu.length; i++) {
				cpu[i-1] = cpu[i];
			}
			m = update;
		}
		m--;
		cpu[cpu.length-1] = (float) JavaSystem.getThreadAverageTime();
		ImGui.text("Version: " + os.getVersion() + " Arch:" + os.getArch() + "\nJava: " + Runtime.version());
		
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ImGui.text("----------------------------");
		for(Long threadID : threadMXBean.getAllThreadIds()) {
		    ThreadInfo info = threadMXBean.getThreadInfo(threadID);
		    ImGui.text("Thread name: " + info.getThreadName());
		    ImGui.text("Thread State: " + info.getThreadState());
		    ImGui.text(String.format("CPU time: %s ns", threadMXBean.getThreadCpuTime(threadID)));
		    ImGui.text("----------------------------");
		}
		ImGui.end();
	
	}
	//Memory
	static float[] memory = new float[50];
	static int m2 = 0;
	/**
	 * Opens an ImGui window containing information about Memory usage
	 * @param update length of the histogram
	 */
	public static void Memory_Window(int update) {
		ImGui.setNextWindowSize(500, 100);
		ImGui.begin("Memory:", ImGuiWindowFlags.NoResize);
		ImGui.plotHistogram("", memory, memory.length, 0, "Total Memory", 0, 100);
		float usage = JavaSystem.getMemoryHeap_Usage();
		float memp = (usage/JavaSystem.getMemoryHeap_Commited());
		if(m2 < 0) {
			for(int i = 1; i < memory.length; i++) {
				memory[i-1] = memory[i];
			}
			m2 = update;
		}
		m2--;
		memory[memory.length-1] = memp*100;
		Color c = memp > 0.9 ? Color.RED : memp > 0.5 ? Color.YELLOW : Color.GREEN;
		ImGui.textColored(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha(),
				"Usage:     " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()/1000000+"MB + "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed()/1000000+"MB\n"
				+"Commited: " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getCommitted()/1000000+"MB + "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getCommitted()/1000000+"MB\n"
				+"Max:      " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()/1000000+"MB + "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax()/1000000+"MB\n"
				+"Init:     " + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getInit()/1000000+"MB + "+ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getInit()/1000000+"MB\n"
				);
		ImGui.end();
	}
	/**
	 * Returns an String with can be appended to the name of the object in question.
	 * @param ob
	 * @return
	 */
	public static String getTrailingBit(Object ob) {
		return "##["+Integer.toHexString(System.identityHashCode(ob))+"]";
	}
	/**
	 * ImGui matrix edit
	 * @param name
	 * @param mat
	 */
	public static void ImGuiMatrix(String name, Matrix4f mat) {
		arr[0] = mat.m00();
		arr[1] = mat.m01();
		arr[2] = mat.m02();
		arr[3] = mat.m03();
		ImGui.dragFloat4(name+"#453", arr);
		mat.m00(arr[0]);
		mat.m01(arr[1]);
		mat.m02(arr[2]);
		mat.m03(arr[3]);
//		ImGui.sameLine();
		arr[0] = mat.m10();
		arr[1] = mat.m11();
		arr[2] = mat.m12();
		arr[3] = mat.m13();
		ImGui.dragFloat4(name+"#454", arr);
		mat.m10(arr[0]);
		mat.m11(arr[1]);
		mat.m12(arr[2]);
		mat.m13(arr[3]);
//		ImGui.sameLine();
		arr[0] = mat.m20();
		arr[1] = mat.m21();
		arr[2] = mat.m22();
		arr[3] = mat.m23();
		ImGui.dragFloat4(name+"#455", arr);
		mat.m20(arr[0]);
		mat.m21(arr[1]);
		mat.m22(arr[2]);
		mat.m23(arr[3]);
//		ImGui.sameLine();
		arr[0] = mat.m30();
		arr[1] = mat.m31();
		arr[2] = mat.m32();
		arr[3] = mat.m33();
		ImGui.dragFloat4(name+"#456", arr);
		mat.m30(arr[0]);
		mat.m31(arr[1]);
		mat.m32(arr[2]);
		mat.m33(arr[3]);
	}
	public static float[] arr = new float[4];
	public static int[] arri = new int[4];
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector4d v) {
		arr[0] = (float) v.x;
		arr[1] = (float) v.y;
		arr[2] = (float) v.z;
		arr[3] = (float) v.w;
		ImGui.dragFloat4(name, arr);
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		v.w = arr[3];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector3d v) {
		arr[0] = (float) v.x;
		arr[1] = (float) v.y;
		arr[2] = (float) v.z;
		ImGui.dragFloat3(name, arr);
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector2d v) {
		arr[0] = (float) v.x;
		arr[1] = (float) v.y;
		ImGui.dragFloat2(name, arr);
		v.x = arr[0];
		v.y = arr[1];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector2i v) {
		arri[0] = v.x;
		arri[1] = v.y;
		ImGui.dragInt2(name, arri);
		v.x = arri[0];
		v.y = arri[1];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector3i v) {
		arri[0] = v.x;
		arri[1] = v.y;
		arri[2] = v.z;
		ImGui.dragInt3(name, arri);
		v.x = arri[0];
		v.y = arri[1];
		v.z = arri[2];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector4i v) {
		arri[0] = v.x;
		arri[1] = v.y;
		arri[2] = v.z;
		arri[3] = v.w;
		ImGui.dragInt4(name, arri);
		v.x = arri[0];
		v.y = arri[1];
		v.z = arri[2];
		v.w = arri[3];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector4f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		arr[3] = v.w;
		ImGui.dragFloat4(name, arr);
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		v.w = arr[3];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector3f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		ImGui.dragFloat3(name, arr);
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiVector(String name, Vector2f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		ImGui.dragFloat2(name, arr);
		v.x = arr[0];
		v.y = arr[1];
	}
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static float ImGuiVector(String name, float v) {
		arr[0] = v;
		ImGui.dragFloat(name, arr);
		return arr[0];
	}
	
	/**
	 * ImGui vector edit
	 * @param name
	 * @param v
	 */
	public static void ImGuiDirection(String name, Vector3f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		ImGui.sliderFloat3(name, arr, -1, 1);
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
	}
	
	/**
	 * ImGui color edit
	 * @param name
	 * @param v
	 * @return
	 */
	public static boolean ImGuiColorE3(String name, Vector3f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		ImGui.colorEdit3(name, arr);
		boolean b = v.x != arr[0] || v.y != arr[1] || v.z != arr[2];
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		return b;
	}
	/**
	 * ImGui color edit
	 * @param name
	 * @param v
	 * @return
	 */
	public static boolean ImGuiColorE4(String name, Vector4f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		arr[3] = v.w;
		ImGui.colorEdit4(name, arr);
		boolean b = v.x != arr[0] || v.y != arr[1] || v.z != arr[2] || v.w != arr[3];
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		v.w = arr[3];
		return b;
	}
	/**
	 * ImGui color picker
	 * @param name
	 * @param v
	 * @return
	 */
	public static boolean ImGuiColorP3(String name, Vector3f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		ImGui.colorPicker3(name, arr);
		boolean b = v.x != arr[0] || v.y != arr[1] || v.z != arr[2];
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		return b;
	}
	/**
	 * ImGui color picker
	 * @param name
	 * @param v
	 * @return
	 */
	public static boolean ImGuiColorP4(String name, Vector4f v) {
		arr[0] = v.x;
		arr[1] = v.y;
		arr[2] = v.z;
		arr[3] = v.w;
		ImGui.colorPicker4(name, arr);
		boolean b = v.x != arr[0] || v.y != arr[1] || v.z != arr[2] || v.w != arr[3];
		v.x = arr[0];
		v.y = arr[1];
		v.z = arr[2];
		v.w = arr[3];
		return b;
	}
	/**
	 * ImGui color slider
	 * @param name
	 * @param v
	 * @return
	 */
	private static float colorSlider(String name, float val) {
		arr[0] = val;
		ImGui.sliderFloat(name, arr, 0, 1);
		return arr[0];
	}
	/**
	 * ImGui color picker for MaterialTypes
	 * @param name
	 * @param v
	 * @return
	 */
	public static void ImGuiColorPMat(String name, MaterialType mat) {
		if(mat instanceof Material) {
			Material m = (Material) mat;
			ImGuiColorP3(name+" - Ambient", m.getAmbientC());
			ImGuiColorP3(name+" - Diffuse", m.getDiffuseC());
			ImGuiColorP3(name+" - Emmisive", m.getEmmisiveC());
			ImGuiColorP3(name+" - Specular", m.getSpecularC());
			m.setSpecularS(colorSlider("Specular I"+getTrailingBit(mat), m.getSpecularS()));
		}
		else if(mat instanceof PBRMaterial) {
			PBRMaterial m = (PBRMaterial) mat;
			ImGuiColorP4("Albedo - "+name, m.getAlbedo());
			m.setAo(colorSlider("Ambient Occlusion - "+name, m.getAo()));
			m.setMetallic(colorSlider("Metallic - "+name, m.getMetallic()));
			m.setRoughness(colorSlider("Roughness - "+name, m.getRoughness()));
			m.setEmissive(colorSlider("Emissive - "+name, m.getEmissive()));
		}
	}
	/**
	 * ImGui color edit for MaterialTypes
	 * @param name
	 * @param v
	 * @return
	 */
	public static void ImGuiColorEMat(String name, MaterialType mat) {
		if(mat instanceof Material) {
			Material m = (Material) mat;
			ImGuiColorE3("Ambient - "+name, m.getAmbientC());
			ImGuiColorE3("Diffuse - "+name, m.getDiffuseC());
			ImGuiColorE3("Emmisive - "+name, m.getEmmisiveC());
			ImGuiColorE3("Specular - "+name, m.getSpecularC());
			m.setSpecularS(colorSlider("Specular I - "+name+getTrailingBit(mat), m.getSpecularS()));
		}
		else if(mat instanceof PBRMaterial) {
			PBRMaterial m = (PBRMaterial) mat;
			ImGuiColorE4("Albedo - "+name, m.getAlbedo());
			m.setAo(colorSlider("Ambient Occlusion - "+name, m.getAo()));
			m.setMetallic(colorSlider("Metallic - "+name, m.getMetallic()));
			m.setRoughness(colorSlider("Roughness - "+name, m.getRoughness()));
			m.setEmissive(colorSlider("Emissive - "+name, m.getEmissive()));
		}
	}
	
	/**
	 * Sets the next element with 'width' width to be centered
	 * @param name
	 * @param v
	 * @return
	 */
	public static void ImGuiCentered(float width) {
		ImGui.setCursorPosX((ImGui.getWindowSizeX() - width) * 0.5f);
	}
	/**
	 * Returns the text width of a given String
	 * @param text
	 * @return
	 */
	public static float ImGuiTextWidth(String text) {
		ImVec2 bounds = new ImVec2();
		ImGui.calcTextSize(bounds, text);
		return bounds.x;
	}

}