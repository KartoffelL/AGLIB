package Kartoffel.Licht.Tools;

import Kartoffel.Licht.Java.freeable;

@Deprecated
public class ImGuiToolsEditor implements freeable{

	@Override
	public void free() {
		// TODO Auto-generated method stub
		
	}
//	
//	protected display display;
//	
//	public static boolean captureMouse = false;
//	public static boolean canCaptureMouse = true;
//	
//	protected JApp app = null;
//	protected DecimalFormat df = new DecimalFormat("0000.00");
//	protected static OperatingSystemMXBean  os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//	protected static ImString input = new ImString();
//	
//	protected  static String fps = "";
//	protected boolean popup_create = false;
//	
//	protected int Assets = 1;
//	
//	protected Model aabbMod;
//	
//	protected Object current = ""; //The currently selected Object
//	
//	protected List<Object> assets = new ArrayList<Object>();
//	protected HashMap<Object, Texture> previewsHashMap = new HashMap<>();
//	
//	protected FrameBuffer previewFB;
//	protected Camera previewCam;
//	
//	public ImGuiToolsEditor init(JApp app) {
//		script.init();
//		this.app = app;
//		this.aabbMod = new Model(new SLineBox());
//		previewFB = new FrameBuffer(app.getWindow(), 100, 100, 0);
//		previewCam = new Camera(1);
//		previewsHashMap.put("DEFAULT", new Texture(Color.BLUE));
//		display = new display();
//		app.getWindow().listeners.add(new GLFWDropCallback() {
//
//			@Override
//			public void invoke(long window, int count, long names) {
//				PointerBuffer nameBuffer = MemoryUtil.memPointerBuffer(names, count);
//			    String[] s = new String[count];
//			    for ( int i = 0; i < count; i++ ) {
//			        s[i] = MemoryUtil.memUTF8(MemoryUtil.memByteBufferNT1(nameBuffer.get(i)));
//			    }
//				if(s.length > 0)
//					for(String f : s)
//						fileDrop(f);
//			}
//			
//		});
//		return this;
//	}
//	
//	protected void fileDrop(String f) {
//		if(f.endsWith(".png") || f.endsWith(".jpg")) {
//			Texture t = new Texture(FileLoader.getImage(f));
//			t.generateMipmaps();
//			t.setName(Tools.getFileName(f));
//			assets.add(t);
//			return;
//		}
//		if(f.endsWith(".gif")) {
//			Animation t = new Animation(FileLoader.getFileD(f));
//			t.setName(Tools.getFileName(f));
//			assets.add(t);
//			return;
//		}
//		if(f.endsWith(".fbx") || f.endsWith(".obj") || f.endsWith(".glb") || f.endsWith(".dae") || f.endsWith(".gltf") || f.endsWith(".blend") || f.endsWith(".usdz")) {
//			if(Tools.ask("Do you want to create an Entity with this Model?", 0, "")) {
//				boolean autoGenTex = Tools.ask("Shall automatically fabricated Textures be made for thee?", 0, "");
//				AssimpLoader al = new AssimpLoader(FileLoader.getFileD(f));
//				Model m = new Model(al);
//				assets.add(m);
//				if(al.materials != null)
//					if(al.materials.length > 0) {
//						if(autoGenTex) {
//							TextureMaterial tm = new TextureMaterial(al.getMaterials());
//							assets.add(tm);
//							GEntity g = new GEntity(tm, m);
//							g.setName("Loaded GEntity");
//							app.getEntities().add(g);
//						}
//						else
//						{
//							TextureArray ta = new TextureArray(6);
//							assets.add(ta);
//							GEntity g = new GEntity(ta, m);
//							g.setName("Loaded GEntity");
//							app.getEntities().add(g);
//						}
//						return;
//					}
//				app.getEntities().add(new GEntity(null, m, "loaded Model"));
//				return;
//			}
//			else {
//				assets.add(new Model(new AssimpLoader(FileLoader.getFileD(f))));
//				return;
//			}
//		}
//		if(new File(f).isDirectory())
//			for(String s : new File(f).list())
//				fileDrop(f+"/"+s);
//	}
//		
//	
//	protected void rep(GEntity o, Object parent, int repp) {
//		String h = ImGuiTools.getTrailingBit(o);
//		if(ImGui.treeNodeEx(getName(o) + h)) {
//			PEntity pty = (PEntity) o.getProperty("Physics");
//			
//			if(ImGui.selectable("Graphics" + h))
//				current = o;
//			
//			if(pty != null)
//				if(ImGui.selectable("Physics" + h))
//					current = pty;
//			ImGui.treePop();
//		}
//	}
//	
//	protected float r_mass = 0;
//	protected boolean r_cp = false;
//	public void draw() {
//			if(app == null)
//				return;
//			ImGui.dockSpaceOverViewport(ImGui.getMainViewport(), ImGuiDockNodeFlags.PassthruCentralNode);
//			if(current instanceof GEntity) {
////				Debug.drawModel(((GEntity)current).getMod(), ((GEntity) current).getTex(), app.getCamera().getViewMatrix(), app.getCamera().getProjection(), ((GEntity) current).getTransformationMatrix(), 0, 1, 1);
//				if(((GEntity) current).getMod() != null) {
//					Debug.COLOR = Color.PINK;
//					Debug.drawAABB(((GEntity) current).getMod().getBounds(), ((GEntity) current).getTransformationMatrix());
//				}
//				captureMouse = Debug.drawTransform(((GEntity)current).getTransformationMatrix(), ((GEntity)current).getPosition(), ((GEntity)current).getRotation(), ((GEntity)current).getScale(), app.getCamera(), app.getWindow(), canCaptureMouse ? 0 : Debug.DT_NOCAPTURE);
//				
//			}
//			
//			else if(current instanceof PEntity) {
////				Debug.drawModel(((PEntity) current).getGentity().getMod(), ((PEntity) current).getGentity().getTex(), app.getCamera().getViewMatrix(), app.getCamera().getProjection(), ((PEntity) current).getGentity().getTransformationMatrix(), 0, 1, 1);
//				if(((PEntity) current).getGentity().getMod() != null) {
//					Debug.COLOR = Color.PINK;
//					Debug.drawAABB(((PEntity) current).getGentity().getMod().getBounds(), ((PEntity) current).getGentity().getTransformationMatrix());
//				}
//				Vector3f pos = ((PEntity) current).getPosition();
////				Vector3f rot = ((PEntity) current).getRotation();
//				captureMouse = Debug.drawTransform(null, pos, null, null, app.getCamera(), app.getWindow(), canCaptureMouse ? 0 : Debug.DT_NOCAPTURE);
//				((PEntity) current).setPosition(pos.x, pos.y, pos.z);
////				((PEntity) current).setRotation(rot.x, rot.y, rot.z);
//				if(captureMouse != r_cp) {
//					if(captureMouse) {
//						r_mass = ((PEntity) current).getMass();
//						((PEntity) current).setMass(0);
//					}
//					else {
//						((PEntity) current).setMass(r_mass);
//					}
//					r_cp = captureMouse;
//				}
//			}
//			else if(current instanceof Camera) {
//				captureMouse = Debug.drawTransform(null, ((Camera)current).getPosition(), ((Camera)current).getRotation(), new Vector3f(1, 1, 1), app.getCamera(), app.getWindow(), canCaptureMouse ? 0 : Debug.DT_NOCAPTURE);
//				Debug.drawCamera((Camera) current);
//			}
//			else if(current instanceof cameraView) {
//				captureMouse = Debug.drawTransform(null, ((cameraView)current).camera.getPosition(), ((cameraView)current).camera.getRotation(), new Vector3f(1, 1, 1), app.getCamera(), app.getWindow(), canCaptureMouse ? 0 : Debug.DT_NOCAPTURE);
//				Debug.drawCamera(((cameraView) current).camera);
//			}
//			else if(current instanceof Light) {
//				captureMouse = Debug.drawTransform(null, ((Light)current).position, ((Light)current).direction, null, app.getCamera(), app.getWindow(), Debug.DT_ROTATION_AS_DIRECTION, canCaptureMouse ? 0 : Debug.DT_NOCAPTURE);
//			}
//			else
//				captureMouse = false;
//			
//			if(current instanceof Model) {
//				Debug.drawModel((Model) current, null, app.getCamera().getViewMatrix(), app.getCamera().getProjection(), null, 0, 1, 1);
//			}
//			
//		
//		ImGui.begin("Editor");
//		ImGui.beginTabBar("bar");
//			
//		if(ImGui.beginTabItem("Objects")) {
//			ImGui.text("Scene:");
//			ImGui.beginChildFrame(1, ImGui.getWindowSizeX(), ImGui.getWindowSizeY());
//					if(ImGui.beginPopupContextWindow("a")) {
//						if(ImGui.selectable("create new...")) {
//							popup_create = true;
//						}
//						ImGui.endPopup();
//					}
//				ImGui.text("Global Objects:");
//				if(ImGui.treeNodeEx("World Light##56456")) {
//					ImGuiTools.ImGuiVector("Color", app.getLightColor());
//					ImGuiTools.ImGuiVector("Direction", app.getLightDir());
//					ImGui.treePop();
//				}
//				
//				
//				ImGui.text("Scene Objects:");
//				for(GEntity o : app.getEntities()) {
//					rep(o, null, 10);
//				}
//				
//				
//			ImGui.endChildFrame();
//			
//			ImGui.endTabItem();
//		}
//	
//		
//		if(ImGui.beginTabItem("System")) {
//			ImGui.text("Version: " + os.getVersion() + " Arch:" + os.getArch() + "\nJava: " + Runtime.version());
//			//FPS
//			if(Tools.every(1.0, 6411461L))
//				fps = df.format(app.getFPS());
//			ImGui.text("FPS: " + fps);
//			//Thread average time
//			ImGui.text("CPU: " + df.format(JavaSystem.getThreadTime())+"ns");
//			//Memory
//			float memp = (JavaSystem.getMemoryHeap_Usage()/JavaSystem.getMemoryHeap_Commited());
//			Color c = memp > 0.9 ? Color.RED : memp > 0.5 ? Color.YELLOW : Color.GREEN;
//			ImGui.textColored(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha(),
//					"Memory:     " + JavaSystem.getMemoryHeap_Usage()/1000000+"MB/"
//					+JavaSystem.getMemoryHeap_Max()/1000000+"MB");
//			//Consol
//			ImGui.beginChildFrame(1, ImGui.getWindowSizeX(), 200);
//			ImGui.beginChild("Log", ImGui.getWindowSizeX(), 170);
//			String[] st = Tools.getLog().split("\n");
//			for(int l = 0; l < Math.min(50, st.length); l++) {
//				String s = st[st.length-l-1];
//				int i = s.indexOf("�");
//				if(i != -1 && s.length() > i+3) {
//					Color color = Logger.fromHex(s.substring(i+1, i+7));
//					ImGui.textColored(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), s.substring(7));
//				}else {
//					ImGui.text(s);
//				}
//			}
//			ImGui.endChild();
//			
//			if(ImGui.inputText("Input", input, ImGuiInputTextFlags.EnterReturnsTrue)) {
//				if(input.get().length() > 0) {
//					Tools.writeToLog(input.get());
//					input.clear();
//				}
//				ImGui.setKeyboardFocusHere(-1);
//			}
//			ImGui.endChildFrame();
//			
//			ImGui.endTabItem();
//		}
//		
//		ImGui.endTabBar();
//		ImGui.end();
//		if(popup_create) {
//			ImGui.setNextWindowPos(app.getWindow().getWidth()/2-100, app.getWindow().getHeight()/2-50);
//			ImGui.setNextWindowSize(200, 100);
//			ImGui.begin("Create new...", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar);
//			if(!ImGui.isWindowFocused())
//				popup_create = false;
//			//Types
//			if(ImGui.selectable("Empty Entity")) {
//				app.getEntities().add((GEntity) (current = new GEntity(null, (Model)null)));
//				popup_create = false;
//			}
//			if(ImGui.selectable("Box")) {
//				app.getEntities().add((GEntity) (current = new GEntity(new TextureMaterial(PBRMaterial.MATERIAL_DEFAULT), new Model(new SBox3D(1, 1, 1)), "Box")));
//				popup_create = false;
//			}
//			if(ImGui.selectable("Physics Sphere")) {
//				PEntity p = new PEntity(new GEntity(new TextureMaterial(PBRMaterial.MATERIAL_DEFAULT), new Model(new SSphere(1, 1, 1, 1, 64)), "Physics Sphere"), 1, 1, 1f);
//				Physics.entitites.add(p);
//				app.getEntities().add(p.getGentity());
//				current = p;
//				popup_create = false;
//			}
//			if(ImGui.selectable("Physics Box")) {
//				PEntity p = new PEntity(new GEntity(new TextureMaterial(PBRMaterial.MATERIAL_DEFAULT), new Model(new SBox3D(1, 1, 1)), "Physics Box"), 1, 2, 1f, 1f, 1f);
//				Physics.entitites.add(p);
//				app.getEntities().add(p.getGentity());
//				current = p;
//				popup_create = false;
//			}
//			if(ImGui.selectable("Sphere")) {
//				app.getEntities().add((GEntity) (current = new GEntity(new TextureMaterial(PBRMaterial.MATERIAL_DEFAULT), new Model(new SSphere(1, 1, 1, 1, 64)), "Sphere")));
//				popup_create = false;
//			}
//			if(ImGui.selectable("Defined Entity - Box")) {
//				app.getEntities().add((GEntity) (current = new GEntity(null, null, "Defined Box").setShape(new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5))));
//				popup_create = false;
//			}
//			if(ImGui.selectable("Defined Entity - Sphere")) {
//				app.getEntities().add((GEntity) (current = new GEntity(null, null, "Defined Sphere").setShape(new DSphere(0, 0, 0, 1))));
//				popup_create = false;
//			}
//			if(ImGui.selectable("Light")) {
//				app.getLights().add((Light) (current = new Light(new Vector3f(), new Vector3f())));
//				popup_create = false;
//			}
//			if(ImGui.selectable("FrameBuffer")) {
//				cameraView cv = new cameraView();
//				cv.camera = new Camera();
//				cv.camera.setName("FrameBuffer Camera");
//				cv.fb = new FrameBuffer(app.getWindow(), 100, 100, 1);
//				cv.fb.setName("FrameBuffer");
//				cv.sprites = false;
//				cv.pp = false;
//				current = cv;
//				popup_create = false;
//			}
//			ImGui.end();
//		}
//		ImGui.begin("Assets", ImGuiWindowFlags.MenuBar);
//		if(ImGui.beginMenuBar()) {
//			if(ImGui.button("open")) {
//				FileDialog.openMultiple(t -> {
//					for(String f : t)
//						fileDrop(f);
//				}, "", new String[][] {{"images", "png", "jpg", "gif", "mp4"}}
//				);
//			}
//			ImGui.endMenuBar();
//		}
//		if(ImGui.beginPopupContextWindow("bb")) {
//			if(ImGui.selectable("create new Texture Container")) {
//				assets.add(new TextureArray(6));
//			}
//			ImGui.endPopup();
//		}
//		int currentElement = 0;
//		int maxWidth = 125;
//		int columns = (int) Math.floor(ImGui.getWindowWidth()/maxWidth);
//		if(columns == 0)
//			columns = 1;
//		ImGui.columns(columns);
//		int totalAssets = 0;
//		for(Object o : assets) {
//			Renderable[] rs = null;
//			if(o instanceof Renderable)
//				rs = new Renderable[] {(Renderable) o};
//			if(o instanceof FrameBuffer)
//				rs = ((FrameBuffer) o).getAllTextures();
//			if(o instanceof cameraView)
//				rs = ((cameraView) o).fb.getAllTextures();
//			if(rs != null) {
//				for(Renderable r : rs) {
//					ImGui.image(r.getID(r instanceof Animation ? -1 : 0), 100, 100, 0, 1, 1, 0);
//					String h = ImGuiTools.getTrailingBit(r);
//					if(ImGui.selectable(r.getName()+h, current == h))
//						current = r;
//					
//					if(ImGui.beginDragDropSource()) {
//						ImGui.setDragDropPayload("image", r);
//						ImGui.image(r.getID(r instanceof Animation ? -1 : 0), 50, 50, 0, 1, 1, 0);
//						ImGui.endDragDropSource();
//					}
//					currentElement++;
//					if(currentElement > Assets/columns) {
//						currentElement = 0;
//						ImGui.nextColumn();
//					}
//					totalAssets++;
//				}
//			}
//			if(o instanceof Model) {
//				ImGui.image(getPreview(o).getID(0), 100, 100, 0, 1, 1, 0);
//				String h = ImGuiTools.getTrailingBit(o);
//				if(ImGui.selectable("Model"+h, current == h))
//					current = o;
//				
//				if(ImGui.beginDragDropSource()) {
//					ImGui.setDragDropPayload("model", o);
//					ImGui.image(getPreview(o).getID(0), 100, 100, 0, 1, 1, 0);
//					ImGui.endDragDropSource();
//				}
//				currentElement++;
//				if(currentElement > Assets/columns) {
//					currentElement = 0;
//					ImGui.nextColumn();
//				}
//				totalAssets++;
//			}
//		}
//		Assets = totalAssets;
//		ImGui.end();
//		if(current != null) {
//			ImGui.begin("Detail", ImGuiWindowFlags.MenuBar);
//			displayObject();
//			ImGui.end();
//		}
//		
//		
//		
//		
//		
//		
//		if(ImGui.beginMainMenuBar()) {
//			if(ImGui.button("open")) {
//				FileDialog.openMultiple(t -> {
//					for(String f : t)
//						fileDrop(f);
//				}, "", new String[][] {{"images", "png", "jpg", "gif", "mp4"}}
//				);
//			}
//			if(ImGui.button("init")) {
//				for(GEntity e : app.getEntities()) {
//					script s = (script) e.getProperty("Script");
//					if(s != null)
//						s.current.init();
//				}
//			}
//			ImGui.endMainMenuBar();
//		}
//		
//		
//	}
//	
//	
//	public Texture getPreview(Object obj) {
//		if(app == null)
//			return null;
//		Texture preview = previewsHashMap.get(obj);
//		if(preview == null){
//			Model mod = null;
//			Renderable tex = null;
//			if(obj instanceof Model)
//				mod = (Model) obj;
//			else if(obj instanceof GEntity) {
//				mod = ((GEntity) obj).getMod();
//				tex = ((GEntity) obj).getTex();
//			}
//			else if(obj instanceof PEntity) {
//				mod = ((PEntity) obj).getGentity().getMod();
//				tex = ((PEntity) obj).getGentity().getTex();
//			}
//			if(mod == null)
//				return previewsHashMap.get("DEFAULT");
//			
//			//Create preview texture
//			FrameBuffer old = app.getWindow().CURRENT_FRAMEBUFFER;
//			Texture texture = new Texture(100, 100, 0);
//			previewFB.setTarget(texture);
//			previewFB.clear();
//			
//			Vector3d center = mod.getBounds().center();
//			float padding = 1.2f;
//			previewCam.getViewMatrix().setLookAt((float)(mod.getBounds().x2*padding-center.x), (float)(mod.getBounds().y2*padding-center.y), (float)(mod.getBounds().z2*padding-center.z), (float)0, (float)0, (float)0, (float)0, (float)1, (float)0);
//			float s = (float) Math.max(mod.getBounds().width(), Math.max(mod.getBounds().height(), mod.getBounds().depth()));
//			Debug.FILL_MODE = 6914;
//			Debug.COLOR = Color.WHITE;
//			Debug.drawModel((Model) mod, tex, previewCam.getViewMatrix(), previewCam.getProjection(), new Matrix4f().scale(1/s).translate((float)-center.x, (float)-center.y, (float)-center.z), 1, 1, 0);
//			Debug.FILL_MODE = 6913;
//			previewsHashMap.put(obj, texture);
//			app.getWindow().bindFrameBuffer(old);
//			return texture;
//		}
//		else
//			return preview;
//	}
//	
//	public String getName(Object o) {
//		return o instanceof namable ? ((namable)o).getName() + "   (" + ((namable)o).getName() + ")" : o.getClass().getSimpleName();
//	}
//	
//	private void ImGuiGEntity(GEntity s, String h) {
//		
//		ImGui.text("Name: " + s.getName());
//		ImGuiTools.ImGuiVector("Position"+h, s.getPosition());
//		ImGuiTools.ImGuiVector("Rotation"+h, s.getRotation());
//		ImGuiTools.ImGuiVector("Scale"+h, s.getScale());
//		ImGui.text("Textures:");
//		if(s.getTex() != null) {
//			if(s.getTex() instanceof MultiTexture) {
//				MultiTexture t = (MultiTexture)s.getTex();
//				for(int i = 0; i < t.getAmount(); i++) {
//					ImGui.image(t.getID(i), 100, 100, 0, 1, 1, 0);
//					if(i % 2 == 1)
//						ImGui.sameLine();
//					if(ImGui.beginDragDropTarget()) {
//						Renderable o = ImGui.acceptDragDropPayload("image");
//						if(o != null) {
//							t.setID(i, o.getID(0));
//							ImGui.endDragDropTarget();
//						}
//					}
//				}
//			}
//			else {
//				Renderable r = s.getTex();
//				ImGui.image(r.getID(r instanceof Animation ? -1 : 0), 100, 100, 0, 1, 1, 0);
//				ImGui.sameLine();
//				if(ImGui.beginDragDropTarget()) {
//					Renderable o = ImGui.acceptDragDropPayload("image");
//					if(o != null) {
//						s.setTex(o);
//					}
//				}
//			}
//			if(ImGui.button("close"))
//				s.setTex(null);
//		}
//		else {
//			ImGui.colorButton("No Textures", new float[] {0.2f, 0.2f, 0.2f});
//			if(ImGui.beginDragDropTarget()) {
//				Renderable o = ImGui.acceptDragDropPayload("image");
//				if(o != null) {
//					s.setTex(o);
//				}
//			}
//		}
//		if(s.getMod() != null) {
//			ImGui.image(getPreview(s).getID(0), 100, 100, 0, 1, 1, 0);
//		}
//		else {
//			ImGui.colorButton("No Model", new float[] {0.2f, 0.2f, 0.2f});
//		}
//		if(ImGui.beginDragDropTarget()) {
//			Model o = ImGui.acceptDragDropPayload("model");
//			if(o != null) {
//				s.setMod(o);
//			}
//		}
//		if(s.getAnimation() != null)
//			if(s.getAnimationManager() != null) {
//				ImGui.text("Animation Manager: " + s.getAnimationManager().toString());
//				ImGuiTools.arr[0] = (float) s.getAnimationManager().getTime();
//				ImGui.dragFloat("Time"+h, ImGuiTools.arr);
//				s.getAnimationManager().setTime(ImGuiTools.arr[0]);
//			} else if(s.getStaticAnimation() != null) {
//				StaticAnimation sa = s.getStaticAnimation();
//				for(int i = 0; i < sa.getTransformAmount(); i++) {
//					Vector3f rot = sa.getTransform(i).getEulerAnglesXYZ(new Vector3f());
//					ImGuiTools.ImGuiVector("Bone " + i + h, rot);
//					sa.getTransform(i).setRotationXYZ(rot.x, rot.y, rot.z);
//				}
//			} else
//				ImGui.text("Animation: " + s.getAnimation().toString());
//		else
//			ImGui.text("Not Animated!");
//		if(s.getShape() != null)
//			ImGui.text("Shape: " + s.toString());
//		else
//			ImGui.text("No Shape");
//		//Script Selection
//		script script = (script)s.getProperty("Script");
//		if(script == null) {
//			s.setProperty("Script", script = new script(s, app));
//		}
//		if(ImGui.button("Open Script"+h))
//			current = script;
//		
//		//////////////////
//	}
//	
//	void displayObject() {
//		String h = ImGuiTools.getTrailingBit(current);
//		if(current instanceof TextureMaterial) {
//			TextureMaterial tm = (TextureMaterial) current;
//			int i = 0;
//			for(MaterialType m : tm.getMaterials()) {
//				ImGui.text("Material: " + i++);
//				ImGuiTools.ImGuiColorEMat("pick"+ImGuiTools.getTrailingBit(m), m);
//			}
//			ImGui.text("Textures:");
//			for(int id = 0; id < tm.getAmount(); id++) {
//				ImGui.image(tm.getID(id), 100, 100, 0, 1, 1, 0, 0, 1, 1, 0);
//			}
//			if(ImGui.beginMenuBar()) {
//				if(ImGui.button("delete")) {
//					assets.remove(current);
//					((Renderable) current).free();
//					current = null;
//				}
//				if(ImGui.button("update"))
//					tm.CMTT();
//				ImGui.endMenuBar();
//			}
//		}
//		else if(current instanceof Renderable) {
//			ImGui.image(((Renderable)current).getID(((Renderable)current) instanceof Animation ? -1 : 0), ImGui.getWindowSizeX()-25, ImGui.getWindowSizeY()-25, 0, 1, 1, 0);
//			if(ImGui.beginMenuBar()) {
//				if(ImGui.button("delete")) {
//					assets.remove(current);
//					((Renderable) current).free();
//					current = null;
//				}
//				ImGui.endMenuBar();
//			}
//		}
//		else if(current instanceof Model) {
//			ImGui.image(getPreview(current).getID(0), ImGui.getWindowSizeX()-25, ImGui.getWindowSizeY()-25, 0, 1, 1, 0);
//		}
//		else if(current instanceof script) {
//			script s = (script) current;
//			ImGui.inputText("Class Name", s.className);
//			if(ImGui.button("Compile"))
//				try {
//					s.compile();
//				} catch (Exception e) {
//					e.printStackTrace();
//					Tools.popup("Error Compiling:\n" + e.getMessage(), 0);
//				}
//			s.editor.render("Script");
//			if(ImGui.isItemHovered())
//				if(ImGui.isKeyDown(GLFWKeys.GLFW_KEY_LEFT_CONTROL.getValue()) && ImGui.isKeyPressed(GLFWKeys.GLFW_KEY_S.getValue()))
//					try {
//						s.compile();
//					} catch (Exception e) {
//						e.printStackTrace();
//						Tools.popup("Error Compiling:\n" + e.getMessage(), 0);
//					}
//		}
//		else if(current instanceof Shader) {
//			Shader s = (Shader) current;
//			ImGui.text("Name: " + s.getName());
//			ImGui.textColored(Color.GREEN.getRGB(), "Uniforms Vertex:");
//			if(s.getUniforms()[0] != null)
//				for(String u : s.getUniforms()[0])
//					ImGui.text(u);
//				else
//					ImGui.text("-");
//			ImGui.textColored(Color.GREEN.getRGB(), "Uniforms Fragment:");
//			if(s.getUniforms()[1] != null)
//				for(String u : s.getUniforms()[1])
//					ImGui.text(u);
//				else
//					ImGui.text("-");
//			ImGui.textColored(Color.GREEN.getRGB(), "Uniforms Geometry:");
//			if(s.getUniforms()[2] != null)
//				for(String u : s.getUniforms()[2])
//					ImGui.text(u);
//				else
//					ImGui.text("-");
//		}
//		else if(current instanceof GEntity) {
//			GEntity s = (GEntity) current;	
//			if(ImGui.beginMenuBar()) {
//				if(ImGui.button("copy")) {
//					app.getEntities().add(((GEntity)current).clone());
//				}
//				if(ImGui.button("delete")) {
//					app.getEntities().remove(current);
//					app.getEntities().remove(current);
//					current = null;
//				}
//				ImGui.endMenuBar();
//			}
//			
//			ImGuiGEntity(s, h);
//		
//		}
//		else if(current instanceof PEntity) {
//			GEntity s = ((PEntity) current).getGentity();
//			PEntity p = ((PEntity) current);
//			ImGui.text("Name: " + s.getName()+h);
//			ImGui.text("PEntity Part:"+h);
//			
//			Vector3f P = p.getPosition();
//			Vector3f R = p.getRotation();
//			ImGuiTools.ImGuiVector("Position"+h, P);
//			ImGuiTools.ImGuiVector("Rotation"+h, R);
//			p.setPosition(P.x, P.y, P.z);
//			p.setRotation(R.x, R.y, R.z);
//			P = p.getPositionMotion();
//			R = p.getRotationMotion();
//			ImGuiTools.ImGuiVector("Linear Motion"+h, P);
//			ImGuiTools.ImGuiVector("Angular Motion"+h, R);
//			p.setPositionMotion(P.x, P.y, P.z);
//			p.setRotationMotion(R.x, R.y, R.z);
//			ImGuiTools.arr[0] = p.getFriction();
//			ImGui.sliderFloat("Friction"+h, ImGuiTools.arr, 0, 1);
//			p.setFriction(ImGuiTools.arr[0]);
//			ImGuiTools.arr[0] = p.getMass();
//			ImGui.sliderFloat("Mass"+h, ImGuiTools.arr, 0, 1);
//			p.setMass(ImGuiTools.arr[0]);
//			ImGui.sameLine();
//			ImGui.text(ImGuiTools.arri[0] == 1 ? "(STATIC_OBJECT)" : ImGuiTools.arri[0] == 2 ? "(KINEMATIC_OBJECT)" : ImGuiTools.arri[0] == 3 ? "(NO_CONTACT_RESPONSE)" : "(CHARACTER_OBJECT)");
//			ImGui.text("Collision Shape"+h);
//			ImGuiTools.arri[0] = p.getType();
////			ImGui.sliderInt("Type"+h, ImGuiTools.arri, 0, 5);
////			p.setType(ImGuiTools.arri[0]);
////			ImGui.sameLine();
////			if(p.getType() == 0) {
////				ImGui.text(" (Plane) "+h);
////				ImGui.dragFloat3("Plane Direction"+h, ImGuiTools.arr, (float)p.getArgs()[0], (float)p.getArgs()[1], (float)p.getArgs()[2]);
////			}
////			else if(p.getType() == 1) {
////				ImGui.text(" (Sphere) "+h);
////				ImGui.dragFloat("Radius"+h, ImGuiTools.arr, (float)p.getArgs()[0]);
////			}
////			else if(p.getType() == 2) {
////				ImGui.text(" (Box) "+h);
////				ImGui.dragFloat3("Box Size"+h, ImGuiTools.arr, (float)p.getArgs()[0], (float)p.getArgs()[1], (float)p.getArgs()[2]);
////			}
////			else if(p.getType() == 3) {
////				ImGui.text(" (Cylinder) "+h);
////				ImGui.dragFloat3("Half Extends"+h, ImGuiTools.arr, (float)p.getArgs()[0], (float)p.getArgs()[1], (float)p.getArgs()[2]);
////			}
////			else if(p.getType() == 4) {
////				ImGui.text(" (Capsule) "+h);
////				ImGui.dragFloat2("Radius, Height"+h, ImGuiTools.arr, (float)p.getArgs()[0], (float)p.getArgs()[1]);
////			}
////			else if(p.getType() == 5) {
////				ImGui.text(" (Cone) "+h);
////				ImGui.dragFloat2("Radius, Height"+h, ImGuiTools.arr, (float)p.getArgs()[0], (float)p.getArgs()[1]);
////			}
////			if(ImGui.button("update Collision Shape"+h)) {
////				p.setCollision(p.getType(), p.getArgs());
////			}
//		}
//		else if(current instanceof Camera) {
//			Camera cam = (Camera) current;
//			
//			ImGuiTools.ImGuiVector("Position"+h, cam.getPosition());
//			ImGuiTools.ImGuiVector("Rotation"+h, cam.getRotation());
//			ImBoolean b = new ImBoolean();
//			b.set(cam.isOrtho());
//			float[] f = new float[] {cam.getProjectionBox().getZ_NEAR()};
//			ImGui.dragFloat("Near Plane"+h, f);
//			cam.getProjectionBox().setZ_NEAR(f[0]);
//			
//			f[0] = cam.getProjectionBox().getZ_FAR();
//			ImGui.dragFloat("Far Plane"+h, f);
//			cam.getProjectionBox().setZ_FAR(f[0]);
//			
//			f[0] = cam.getProjectionBox().getAspectRatio();
//			ImGui.dragFloat("Aspect Ratio"+h, f);
//			cam.getProjectionBox().setAspectRatio(f[0]);
//			
//			ImGui.checkbox("Orthogonal"+h, b);
//			cam.setOrtho(b.get());
//			if(b.get()) {
//				f[0] = cam.getProjectionBox().getPlaneSize();
//				ImGui.dragFloat("Size"+h, f);
//				cam.getProjectionBox().setPlaneSize(f[0]);
//			}
//			else {
//				f[0] = cam.getProjectionBox().getFOV();
//				ImGui.dragFloat("FOV"+h, f);
//				cam.getProjectionBox().setFOV(f[0]);
//			}
//			
//			cam.update();
//		}
//		else if(current instanceof cameraView) {
//			cameraView c = (cameraView) current;
//			
//				ImBoolean b = new ImBoolean(c.pp);
//				ImGui.checkbox("Post Processing"+h, b);
//				c.pp = b.get();
//				b.set(c.sprites);
//				ImGui.sameLine();
//				ImGui.checkbox("Sprites"+h, b);
//				c.sprites = b.get();
//				
//				if(ImGui.treeNodeEx("Camera" + ImGuiTools.getTrailingBit(c))) {
//					ImGuiTools.ImGuiVector("Position"+h, c.camera.getPosition());
//					ImGuiTools.ImGuiVector("Rotation"+h, c.camera.getRotation());
//					b.set(c.camera.isOrtho());
//					ImGui.checkbox("Orthogonal"+h, b);
//					c.camera.setOrtho(b.get());
//					float f[] = new float[1];
//					if(b.get()) {
//						f[0] = c.camera.getProjectionBox().getPlaneSize();
//						ImGui.dragFloat("Size"+h, f);
//						c.camera.getProjectionBox().setPlaneSize(f[0]);
//					}
//					else {
//						f[0] = c.camera.getProjectionBox().getFOV();
//						ImGui.dragFloat("FOV"+h, f);
//						c.camera.getProjectionBox().setFOV(f[0]);
//					}
//					ImGui.treePop();
//				}
//				if(ImGui.treeNodeEx("FrameBuffer" + ImGuiTools.getTrailingBit(c))) {
//					int[] d = new int[] {c.fb.getWidth(), c.fb.getHeight()};
//					ImGui.dragInt2("res"+ImGuiTools.getTrailingBit(c), d);
//					if(d[0] != c.fb.getWidth() || d[1] != c.fb.getHeight())
//						c.fb.reSize(d[0], d[1]);
//					for(FBOTexture t : c.fb.getAllTextures()) {
//						ImGui.image(t.getID(0), 100, 100, 0, 1, 1, 0);
//						if(ImGui.selectable(t.getName()+h, current == h))
//							current = t;
//						
//						if(ImGui.beginDragDropSource()) {
//							ImGui.setDragDropPayload("image", t);
//							ImGui.image(t.getID(0), 50, 50, 0, 1, 1, 0);
//							ImGui.endDragDropSource();
//						}
//					}
//					ImGui.treePop();
//				}
//		
//		}
//		else if(current instanceof Light) {
//			Light s = (Light) current;
//				ImGuiTools.ImGuiVector("Color#"+h, s.color);
//				ImGuiTools.ImGuiVector("Position"+h, s.position);
//				ImGuiTools.ImGuiVector("Direction"+h, s.direction);
//				s.cutoff = ImGuiTools.ImGuiVector("cutoff"+h, s.cutoff);
//				s.edge = ImGuiTools.ImGuiVector("edge"+h, s.edge);
//				ImBoolean b = new ImBoolean(s.spotlight);
//				ImGui.checkbox("Spotlight", b);
//				s.spotlight = b.get();
//		}
//	}
//	
//	public void update(double delta) {
//		
////		//Updating
////		List<String> names = new ArrayList<>();
////		List<Integer> counts = new ArrayList<>();
////		for(Object o : app.getEntities()) {
////			if(!EntityEntry.contains(o))
////				continue;
////			String name = getName(o);
////			if(names.contains(name)) {
////				int index = names.indexOf(name);
////				counts.set(index, ((int)counts.get(index))+1);
////			}
////			else {
////				names.add(name);
////				counts.add(1);
////			}
////			if(o instanceof cameraView) {
////				cameraView c = (cameraView) o;
////				try {app.paintAll(c.fb, c.camera, c.pp, c.sprites);} catch (Exception e) {}
////			}
////		}
////		for(int i = 0; i < counts.size(); i++) {
////			if(counts.get(i) > 5) {
////				List<Object> list = new ArrayList<>();
////				for(Object o : objects) {
////					String name = getName(o);
////					if(name.equals(names.get(i))) {
////						list.add(o);
////					}
////				}
////				boolean found = false;
////				for(folder f : folders) {
////					if(f.getSName().equals(names.get(i))) {
////						f.getObjects().clear();
////						f.getObjects().addAll(list);
////						found = true;
////					}
////				}
////				if(!found) {
////					folder folder = new folder(list, names.get(i));
////					folders.add(folder);
////					objects.add(folder);
////				}
////			}
////		}
//		
//		for(Object o : app.getEntities()) { //Wont work
//			if(o instanceof cameraView) {
//				cameraView c = (cameraView) o;
//				try {app.paintAll(c.fb, c.camera, c.pp, c.sprites, false);} catch (Exception e) {}
//			}
//		}
//		//Mouse Picking
//		if(app.getWindow().getCallback_Key().isMouseButtonDown(1)) {
//			Vector4f v = CameraUtils.getRay((float)app.getWindow().getCallback_Cursor().getX()/app.getWindow().getWidth()*2-1, 2-(float)app.getWindow().getCallback_Cursor().getY()/app.getWindow().getHeight()*2-1, app.getCamera());
//			HitInfo point = Physics.intersectionBulletNearest(new Ray(app.getCamera().getPositionDistanced(), v));
//			if(point.entity != null)
//				current = point.entity;
//		}
//	}
//	
//	public void free() {
//		script.running = false;
//		previewFB.free();
//		previewsHashMap.forEach(new BiConsumer<Object, Texture>() {
//
//			@Override
//			public void accept(Object t, Texture u) {
//				u.free();
//			}
//		});
//	}

}