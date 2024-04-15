package Kartoffel.Licht.Tools.ImGE;

@Deprecated
public class script {
//	public volatile TextEditor editor = new TextEditor();
//	public volatile ImString className = new ImString("Script");
//	public volatile actrice current = null;
//	public volatile GEntity entity;
//	public volatile JApp app;
//	
//	
//	public static volatile List<Runnable> toCompile = new ArrayList<>();
//	public static Thread compilerThread;
//	public static volatile boolean running = false;
//	
//	public static List<script> global = new ArrayList<>();
//	
//	public script(GEntity s, JApp app) {
//		global.add(this);
//		this.entity = s;
//		this.app = app;
//		editor.setText(""
//				+ "import Kartoffel.Licht.Tools.ImGE.actrice;\r\n"
//				+ "import Kartoffel.Licht.Engine.JApp;\r\n"
//				+ "\r\n"
//				+ "public class Script extends actrice{\r\n"
//				+ "\r\n"
//				+ "	@Override\r\n"
//				+ "	public void init() {\r\n"
//				+ "		System.out.println(\"Init Script!\");\r\n"
//				+ "	}\r\n"
//				+ "\r\n"
//				+ "	@Override\r\n"
//				+ "	public void update(double delta) {\r\n"
//				+ "		// TODO Auto-generated method stub\r\n"
//				+ "		\r\n"
//				+ "	}\r\n"
//				+ "\r\n"
//				+ "	@Override\r\n"
//				+ "	public void end() {\r\n"
//				+ "		System.out.println(\"Terminated Script!\");\r\n"
//				+ "	}\r\n"
//				+ "\r\n"
//				+ "}\r\n"
//				+ "");
//	}
//	
//	public static void update(double delta) {
//		for(script s : global) {
//			if(s.current != null)
//				s.current.update(delta);
//		}
//	}
//	
//	public static void init() {
//		if(compilerThread == null) {
//			running = true;
//			compilerThread = new Thread() {
//				@Override
//				public void run() {
//					while(running)
//						if(toCompile.size() != 0)
//							toCompile.remove(0).run();
//				}
//			};
//			compilerThread.start();
//		}
//	}
//	
//	public void compile() throws Exception {
//		toCompile.add(new Runnable() {
//			
//			String error = "";
//			String normal = "";
//			@Override
//			public void run() {
//				OutputStream oos = new OutputStream() {
//					
//					@Override
//					public void write(int b) throws IOException {
//						normal += (char)b;
//					}
//				};
//				OutputStream err_oos = new OutputStream() {
//					
//					@Override
//					public void write(int b) throws IOException {
//						error += (char)b;
//					}
//				};
//				try {
//					// Save source in .java file.
//					File root = Files.createTempDirectory("java").toFile();
//					File sourceFile = new File(root, "Script.java");
//					sourceFile.getParentFile().mkdirs();
//					Files.write(sourceFile.toPath(), editor.getText().getBytes(StandardCharsets.UTF_8));
//
//					// Compile source file.
//					JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//					compiler.run(null, oos, err_oos, sourceFile.getPath());
//
//					// Load and instantiate compiled class.
//					URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
//					Class<?> c = Class.forName("Script", true, classLoader);
//					if(actrice.class.isAssignableFrom(c)) {
//						@SuppressWarnings("unchecked")
//						Class<actrice> cls = (Class<actrice>) Class.forName("Script", true, classLoader); // Should print "hello".
//						actrice a = null;
//						try {
//							for(Constructor<?> con : cls.getConstructors())
//								a = (actrice) con.newInstance();
//							if(a == null)
//								throw new Exception("Failed!");
//						} catch (Exception e) {
//							Tools.popup("Failed to generate Instance!: " + e.getMessage(), 0);
//						}
//						current = a;
//						current.entity = entity;
//						current.app = app;
//						current.init();
//						Tools.popup("Compiled!: " + error + "\n" + normal, 1);
//					}
//					else
//						Tools.popup("Main class has to interface 'actrise'", 0);
//				} catch (Throwable e) {
//					Tools.popup("Failed to Compile!: \n" + error + "\n" + normal + "\n" + e.getMessage(), 0);
//				}
//			}
//		});
//		
//	}
}