package Kartoffel.Licht.Media;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.openxr.EXTDebugUtils;
import org.lwjgl.openxr.KHROpenGLEnable;
import org.lwjgl.openxr.XR10;
import org.lwjgl.openxr.XrAction;
import org.lwjgl.openxr.XrActionCreateInfo;
import org.lwjgl.openxr.XrActionSet;
import org.lwjgl.openxr.XrActionSetCreateInfo;
import org.lwjgl.openxr.XrActionSpaceCreateInfo;
import org.lwjgl.openxr.XrActionStateBoolean;
import org.lwjgl.openxr.XrActionStateFloat;
import org.lwjgl.openxr.XrActionStateGetInfo;
import org.lwjgl.openxr.XrActionStatePose;
import org.lwjgl.openxr.XrActionStateVector2f;
import org.lwjgl.openxr.XrActionSuggestedBinding;
import org.lwjgl.openxr.XrActionsSyncInfo;
import org.lwjgl.openxr.XrActiveActionSet;
import org.lwjgl.openxr.XrApplicationInfo;
import org.lwjgl.openxr.XrCompositionLayerProjection;
import org.lwjgl.openxr.XrCompositionLayerProjectionView;
import org.lwjgl.openxr.XrDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.openxr.XrDebugUtilsMessengerCallbackEXTI;
import org.lwjgl.openxr.XrDebugUtilsMessengerCreateInfoEXT;
import org.lwjgl.openxr.XrEventDataBuffer;
import org.lwjgl.openxr.XrEventDataEventsLost;
import org.lwjgl.openxr.XrEventDataInstanceLossPending;
import org.lwjgl.openxr.XrEventDataInteractionProfileChanged;
import org.lwjgl.openxr.XrEventDataReferenceSpaceChangePending;
import org.lwjgl.openxr.XrExtent2Di;
import org.lwjgl.openxr.XrFovf;
import org.lwjgl.openxr.XrFrameBeginInfo;
import org.lwjgl.openxr.XrFrameEndInfo;
import org.lwjgl.openxr.XrFrameState;
import org.lwjgl.openxr.XrFrameWaitInfo;
import org.lwjgl.openxr.XrGraphicsBindingOpenGLWin32KHR;
import org.lwjgl.openxr.XrGraphicsRequirementsOpenGLKHR;
import org.lwjgl.openxr.XrHapticActionInfo;
import org.lwjgl.openxr.XrHapticBaseHeader;
import org.lwjgl.openxr.XrHapticVibration;
import org.lwjgl.openxr.XrInstance;
import org.lwjgl.openxr.XrInstanceCreateInfo;
import org.lwjgl.openxr.XrInteractionProfileState;
import org.lwjgl.openxr.XrInteractionProfileSuggestedBinding;
import org.lwjgl.openxr.XrOffset2Di;
import org.lwjgl.openxr.XrPosef;
import org.lwjgl.openxr.XrQuaternionf;
import org.lwjgl.openxr.XrRect2Di;
import org.lwjgl.openxr.XrReferenceSpaceCreateInfo;
import org.lwjgl.openxr.XrSession;
import org.lwjgl.openxr.XrSessionActionSetsAttachInfo;
import org.lwjgl.openxr.XrSessionBeginInfo;
import org.lwjgl.openxr.XrSessionCreateInfo;
import org.lwjgl.openxr.XrSpace;
import org.lwjgl.openxr.XrSpaceLocation;
import org.lwjgl.openxr.XrSwapchain;
import org.lwjgl.openxr.XrSwapchainCreateInfo;
import org.lwjgl.openxr.XrSwapchainImageAcquireInfo;
import org.lwjgl.openxr.XrSwapchainImageBaseHeader;
import org.lwjgl.openxr.XrSwapchainImageOpenGLKHR;
import org.lwjgl.openxr.XrSwapchainImageReleaseInfo;
import org.lwjgl.openxr.XrSwapchainImageWaitInfo;
import org.lwjgl.openxr.XrSwapchainSubImage;
import org.lwjgl.openxr.XrSystemGetInfo;
import org.lwjgl.openxr.XrVector2f;
import org.lwjgl.openxr.XrVector3f;
import org.lwjgl.openxr.XrView;
import org.lwjgl.openxr.XrViewConfigurationView;
import org.lwjgl.openxr.XrViewLocateInfo;
import org.lwjgl.openxr.XrViewState;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Texture.FBOTexture;
import Kartoffel.Licht.Tools.Tools;

public class VR {
	
    public static final int
    XR_SESSION_STATE_UNKNOWN      = 0,
    XR_SESSION_STATE_IDLE         = 1,
    XR_SESSION_STATE_READY        = 2,
    XR_SESSION_STATE_SYNCHRONIZED = 3,
    XR_SESSION_STATE_VISIBLE      = 4,
    XR_SESSION_STATE_FOCUSED      = 5,
    XR_SESSION_STATE_STOPPING     = 6,
    XR_SESSION_STATE_LOSS_PENDING = 7,
    XR_SESSION_STATE_EXITING      = 8;
    
    public static final int
    XR_REFERENCE_SPACE_TYPE_VIEW  = 1,
    XR_REFERENCE_SPACE_TYPE_LOCAL = 2,
    XR_REFERENCE_SPACE_TYPE_STAGE = 3;
    
    public static final int
    XR_ACTION_TYPE_BOOLEAN_INPUT    = 1,
    XR_ACTION_TYPE_FLOAT_INPUT      = 2,
    XR_ACTION_TYPE_VECTOR2F_INPUT   = 3,
    XR_ACTION_TYPE_POSE_INPUT       = 4,
    XR_ACTION_TYPE_VIBRATION_OUTPUT = 100;
    
    public static final String
    INTERACTPROF_DEFAULT = "/interaction_profiles/khr/simple_controller",
    INTERACTPROF_OCULUS = "/interaction_profiles/oculus/touch_controller";
    
    public static final String
    SP_GAMEPAD = "/user/gamepad",
    SP_HAND_LEFT = "/user/hand/left",
    SP_HAND_RIGHT = "/user/hand/right";
    
    public static final String
    NA_IN_SELECT_CLICK = "/input/select/click",
    NA_IN_MENU_CLICK = "/input/menu/click",
    NA_IN_GRIP_POSE = "/input/grip/pose",
    NA_IN_AIM_POSE = "/input/aim/pose",
    NA_OUT_HAPTIC = "/output/haptic",
    NA_IN_EXT_SQUEEZE_VALUE = "/input/squeeze/value",
    NA_IN_EXT_A_CLICK = "/input/a/click",
    NA_IN_EXT_B_CLICK = "/input/b/click",
    NA_IN_EXT_X_CLICK = "/input/x/click",
    NA_IN_EXT_Y_CLICK = "/input/y/click",
    NA_IN_EXT_TRIGGER_VALUE = "/input/trigger/value";
    
    public static boolean DEBUGMESSAGES = false;
	
	public static interface callback {
		public void stateChanged(int stateChange, long time);
		public void render(int index, int frameBuffer, int width, int height, int format, Matrix4f viewMat, Matrix4f projMat);
		public actionFamilyConstructor getActions();
	}
	
	private static boolean wantsExit = false;
	
	private static XrInstance instance;
	private static XrSession session;
	private static XrSpace localSace;
	private static long systemID;
	private static boolean sessionRunning = false;
	private static boolean isConnected = false;
	private static GraphicWindow window;
	private static int timeout;
	private static long timestep;
	private static callback call;
	private static int viewConfig = 0;
	
	public static class view {
		public XrViewConfigurationView configView;
		public XrSwapchain swapchain;
		public long format;
		public XrSwapchainImageOpenGLKHR.Buffer images;
		public int width;
		public int height;
		public int frameBuffer;
		public int currentImage = 0;
		public int getImage(int index) {
			return images.get(index).image();
		}
		public int getCurrentImage() {
			return images.get(currentImage).image();
		}
		public FBOTexture depthStencil;
	}
	private static view[] views;
	private static XrView.Buffer xrViews;
	private static XrViewConfigurationView.Buffer temp;
	 
	private static float near = 0.1f, far = 100.0f;
	
	private static XrActionSet actionSet;
	public static class action {
		public XrAction action;
		public long path;
		public boolean isActive = false;
		public boolean isValid = false;
		public final int type;
		public actionConstructor constructor;
		public action(int type, XrAction action, String path) {
			this.type = type;			this.action = action;
			LongBuffer lb = MemoryStack.stackCallocLong(1);
			int code = XR10.xrStringToPath(instance, path, lb);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to create action object(Path not found)!" + code);
			this.path = lb.get(0);
		}
		public void free() {
			XR10.xrDestroyAction(action);
		}
	}
	public static class actionPose extends action {
		public XrSpace location;
		public boolean positionValid;
		public boolean orientationValid;
		public Vector3f position = new Vector3f();
		public Quaternionf orientation = new Quaternionf();
		public actionPose(XrAction action, String path) {
			super(XR_ACTION_TYPE_POSE_INPUT, action, path);
			location = createActionSpace(action, this.path);
		}
		@Override
		public void free() {
			super.free();
			XR10.xrDestroySpace(location);
		}
	}
	public static class actionFloat extends action {
		public float value;
		public actionFloat(XrAction action, String path) {
			super(XR_ACTION_TYPE_FLOAT_INPUT, action, path);
		}
	}
	public static class actionBoolean extends action {
		public boolean value;
		public actionBoolean(XrAction action, String path) {
			super(XR_ACTION_TYPE_BOOLEAN_INPUT, action, path);
		}
	}
	public static class actionVec2 extends action {
		public Vector2f value;
		public actionVec2(XrAction action, String path) {
			super(XR_ACTION_TYPE_VECTOR2F_INPUT, action, path);
			value = new Vector2f();
		}
	}
	public static class actionoVibr extends action {
		public float intensity = 0.5f;
		public long duration = XR10.XR_MIN_HAPTIC_DURATION;
		public float frequency = XR10.XR_FREQUENCY_UNSPECIFIED;
		boolean triggered = false;
		boolean triggeredstop = false;
		public void trigger() {
			this.triggered = true;
		}
		public void stop() {
			this.triggeredstop = true;
		}
		public actionoVibr(XrAction action, String path) {
			super(XR_ACTION_TYPE_VIBRATION_OUTPUT, action, path);
		}
	}
	public static abstract class actionConstructor {
		public String id = "actiona";
		public String name = "Action A";
		public int type = XR_ACTION_TYPE_POSE_INPUT;
		public String subpath = "/user/hand/left";
		public String nativeAction = "/user/hand/left/input/grip/pose";
		public action action;
		public actionConstructor(String id, String name, int type, String subpath, String nativeAction) {
			this.id = id;
			if(id != id.toLowerCase())
				throw new RuntimeException("ID is not allowed to contain upper-case letters!");
			this.name = name;
			if(type != 0)
				this.type = type;
			if(subpath != null)
				this.subpath = subpath;
			if(nativeAction != null)
				this.nativeAction = nativeAction;
		}
		public actionConstructor() {
			
		}
		public abstract void setValue(action action);
	}
	public static class actionSetConstructor {
		String id = "aglib-actionset";
		String name = "Standart Action Set";
		public actionSetConstructor() {
			
		}
		public actionSetConstructor(String id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	public static class actionBindingsConstructor {
		String interactionProfile = "/interaction_profiles/khr/simple_controller";
		public actionConstructor[] actions = null;
		public actionBindingsConstructor(String interactionProfile, actionConstructor...actions) {
			this.actions = actions;
			if(interactionProfile != null)
				this.interactionProfile = interactionProfile;
		}
	}
	public static class actionFamilyConstructor {
		public actionSetConstructor actionSet = new actionSetConstructor();
		public ArrayList<actionConstructor> actions = new ArrayList<VR.actionConstructor>();
		public ArrayList<actionBindingsConstructor> actionBindings = new ArrayList<VR.actionBindingsConstructor>();
		public actionFamilyConstructor(boolean defaultAction) {
			if(defaultAction) {
				actions.add(new actionConstructor() {@Override public void setValue(action action) {}});
				actionBindings.add(new actionBindingsConstructor(null, actions.get(0)));
			}
		}
		public actionFamilyConstructor() {
			
		}
	}
	private static List<action> actions = new ArrayList<action>();
	//Breaks
//	public static void connectAsync(GraphicWindow window, int timeout, long timeStep, callback call) {
//		new Thread(()->connect(window, timeout, timeStep, call)).start();
//	}
	public static void connect(GraphicWindow window, int timeout, long timeStep, callback call) {
		VR.window = window;
		VR.timeout = timeout;
		VR.call = call;
		VR.timestep = timeStep;
		try(MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pointerbuffer = stack.callocPointer(1);
			LongBuffer longbuffer = stack.callocLong(1);
			int status = 0;
			if(instance == null){
				XrInstanceCreateInfo instance_ci = new XrInstanceCreateInfo(stack.calloc(328));
				XrApplicationInfo application_ci = new XrApplicationInfo(stack.calloc(272));
				application_ci.set(stack.ASCII("application"), 0, stack.ASCII("engine"), 123, XR10.XR_CURRENT_API_VERSION);
				instance_ci.set(XR10.XR_TYPE_INSTANCE_CREATE_INFO, 0, 0, application_ci, /*Apilayers*/stack.callocPointer(0), /*Extensions*/stack.pointers(stack.ASCII("XR_KHR_opengl_enable"), stack.ASCII("XR_EXT_debug_utils")));
				status = XR10.xrCreateInstance(instance_ci, pointerbuffer);
				if(!XR10.XR_SUCCEEDED(status))
					throw new RuntimeException("Failed to create Instance with code " + status + "!");
				instance = new XrInstance(pointerbuffer.get(0), instance_ci);
				{
					XrDebugUtilsMessengerCreateInfoEXT inf = new XrDebugUtilsMessengerCreateInfoEXT(stack.calloc(48));
					XrDebugUtilsMessengerCallbackEXTI calbk = new XrDebugUtilsMessengerCallbackEXTI() {
	
						@Override
						public int invoke(long messageSeverity, long messageTypes, long callbackData, long userData) {
							if(!DEBUGMESSAGES)
								return 0;
							XrDebugUtilsMessengerCallbackDataEXT d = new XrDebugUtilsMessengerCallbackDataEXT(MemoryUtil.memByteBuffer(callbackData, 72));
							System.out.println("[XR_DEBUG]" + d.messageString());
							return 0;
						}
						
					};
					inf.set(EXTDebugUtils.XR_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT, 0, EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT, EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_CONFORMANCE_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT|EXTDebugUtils.XR_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT, calbk, 0);
					EXTDebugUtils.xrCreateDebugUtilsMessengerEXT(instance, inf, pointerbuffer);
				}
			}
			XrSystemGetInfo system_info = new XrSystemGetInfo(stack.calloc(24));
			system_info.set(XR10.XR_TYPE_SYSTEM_GET_INFO, 0, XR10.XR_FORM_FACTOR_HEAD_MOUNTED_DISPLAY); //1:Head mounted display, 2 = handheld display
			int t = 0;
			
			do {
				t++;
				status = XR10.xrGetSystem(instance, system_info, longbuffer);
				try {Thread.sleep(timeStep);} catch (InterruptedException e) {}
				if(t > timeout && timeout != -1)
					throw new RuntimeException("Timed out!");
			} while(!XR10.XR_SUCCEEDED(status));
			Tools.conm("Connected Headset!");
			systemID = longbuffer.get(0);
			GLFW.glfwGetPlatform(); //plattform
			XrSessionCreateInfo session_ci = new XrSessionCreateInfo(stack.calloc(32));
			long gaddr = 0;
			if(GLFW.glfwGetPlatform() == GLFW.GLFW_PLATFORM_WIN32) {
				XrGraphicsBindingOpenGLWin32KHR grabin = new XrGraphicsBindingOpenGLWin32KHR(stack.calloc(32));
				grabin.set(KHROpenGLEnable.XR_TYPE_GRAPHICS_BINDING_OPENGL_WIN32_KHR, 0, window.WINDOW_ID, window.WINDOW_ID);
				gaddr = grabin.address();
			}
			XrGraphicsRequirementsOpenGLKHR grareq = new XrGraphicsRequirementsOpenGLKHR(stack.calloc(32));
			grareq.set(KHROpenGLEnable.XR_TYPE_GRAPHICS_REQUIREMENTS_OPENGL_KHR, 0, XR10.XR_MAKE_VERSION(3, 3, 0), XR10.XR_MAKE_VERSION(3, 3, 0));
			status = KHROpenGLEnable.xrGetOpenGLGraphicsRequirementsKHR(instance, systemID, grareq);
			if(!XR10.XR_SUCCEEDED(status))
				throw new RuntimeException("Failed to get openGL graphics requirements! " + status);
			session_ci.set(XR10.XR_TYPE_SESSION_CREATE_INFO, gaddr, 0, systemID);
			status = XR10.xrCreateSession(instance, session_ci, pointerbuffer);
			if(!XR10.XR_SUCCEEDED(status))
				throw new RuntimeException("Failed to create Session! " + status);
			session = new XrSession(pointerbuffer.get(0), instance);
			localSace = createSpace(XR_REFERENCE_SPACE_TYPE_STAGE);
			
			{
				actionFamilyConstructor f = call.getActions();
				if(f != null) {
					actionSet = createActionSet(f.actionSet.id, f.actionSet.name);
					
					for(actionConstructor ac : f.actions) {
						XrAction actionA = createAction(actionSet, ac.id, ac.name, ac.type, ac.subpath);
						action act;
						switch (ac.type) {
							case XR_ACTION_TYPE_POSE_INPUT: {
								act = new actionPose(actionA, ac.subpath);
								break;
							}
							case XR_ACTION_TYPE_FLOAT_INPUT: {
								act = new actionFloat(actionA, ac.subpath);
								break;
							}
							case XR_ACTION_TYPE_BOOLEAN_INPUT: {
								act = new actionBoolean(actionA, ac.subpath);
								break;
							}
							case XR_ACTION_TYPE_VECTOR2F_INPUT: {
								act = new actionVec2(actionA, ac.subpath);
								break;
							}
							case XR_ACTION_TYPE_VIBRATION_OUTPUT: {
								act = new actionoVibr(actionA, ac.subpath);
								break;
							}
							default:
								throw new IllegalArgumentException("Action not Found: " + ac.type);
						}
						actions.add(act);
						act.constructor = ac;
						act.constructor.action = act;
					}
					for(actionBindingsConstructor c : f.actionBindings) {
						XrAction[] acs = new XrAction[c.actions.length];
						String[] nacs = new String[c.actions.length];
						for(int i = 0; i < c.actions.length; i++) {
							nacs[i] = c.actions[i].nativeAction;
							acs[i] = actions.get(i).action;
						}
						suggestBinding(c.interactionProfile, acs, nacs);
					}
				}
			}
		}
		isConnected = true;
	}
	
	public static XrActionSet createActionSet(String name, String localizedName) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrActionSetCreateInfo actionset_ci = new XrActionSetCreateInfo(stack.calloc(216));
			actionset_ci.set(XR10.XR_TYPE_ACTION_SET_CREATE_INFO, 0, stack.ASCII(name), stack.ASCII(localizedName), 0);
			PointerBuffer pointerbuffer = stack.callocPointer(1);
			int status = XR10.xrCreateActionSet(instance, actionset_ci, pointerbuffer);
			if(!XR10.XR_SUCCEEDED(status))
				throw new RuntimeException("Failed to create actionset! " + status);
			return new XrActionSet(pointerbuffer.get(0), instance);
		}
	}
	
	public static XrAction createAction(XrActionSet actionset, String name, String localizedName, int type, String...subactionpaths) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrActionCreateInfo action_ci = new XrActionCreateInfo(stack.calloc(224));
			LongBuffer lb = stack.callocLong(subactionpaths.length);
			for(int i = 0; i < subactionpaths.length; i++) {
				int status = XR10.xrStringToPath(instance, subactionpaths[i], lb);
				if(!XR10.XR_SUCCEEDED(status))
					throw new RuntimeException("Failed to create Action (Failed at subactionpath " + i + ") " + status);
				lb.put(i, lb.get(0));
			}
			action_ci.set(XR10.XR_TYPE_ACTION_CREATE_INFO, 0, stack.ASCII(name), type, subactionpaths.length, lb, stack.ASCII(localizedName));
			PointerBuffer pointerbuffer = stack.callocPointer(1);
			int status = XR10.xrCreateAction(actionset, action_ci, pointerbuffer);
			if(!XR10.XR_SUCCEEDED(status))
				throw new RuntimeException("Failed to create Action with code " + status + "!");
			return new XrAction(pointerbuffer.get(0), actionset);
		}
	}
	
	public static void suggestBinding(String interactionProfile, XrAction[] actions, String[] bindings) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrInteractionProfileSuggestedBinding ipsb = new XrInteractionProfileSuggestedBinding(stack.calloc(40));
			XrActionSuggestedBinding.Buffer buff = new XrActionSuggestedBinding.Buffer(stack.calloc(16*actions.length));
			LongBuffer lb = stack.callocLong(1);
			for(int i = 0; i < actions.length; i++) {
				XrActionSuggestedBinding b = new XrActionSuggestedBinding(stack.calloc(16));
				int status = XR10.xrStringToPath(instance, bindings[i], lb);
				if(!XR10.XR_SUCCEEDED(status)) {
					b.close(); //Ressource leak warning fix
					throw new RuntimeException("Failed to suggest Binding! (Failed at binding " + i + ") " + status);
				}
				b.set(actions[i], lb.get(0));
				buff.put(i, b);
			}
			XR10.xrStringToPath(instance, interactionProfile, lb);
			ipsb.set(XR10.XR_TYPE_INTERACTION_PROFILE_SUGGESTED_BINDING, 0, lb.get(0), buff);
			int code = XR10.xrSuggestInteractionProfileBindings(instance, ipsb);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to suggest interaction profile bindings! " + code);
		}
	}
	
	public static void attachActionSet(XrActionSet...actionsets) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrSessionActionSetsAttachInfo att_info = new XrSessionActionSetsAttachInfo(stack.calloc(32));
			PointerBuffer pb = stack.callocPointer(actionsets.length);
			for(int i = 0; i < actionsets.length; i++)
				pb.put(i, actionsets[i].address());
			att_info.set(XR10.XR_TYPE_SESSION_ACTION_SETS_ATTACH_INFO, 0, pb);
			XR10.xrAttachSessionActionSets(session, att_info);
		}
	}
	
	public static long recordCurrentBindings(long toplevelUserPath) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrInteractionProfileState state = new XrInteractionProfileState(stack.calloc(1));
			state.set(XR10.XR_TYPE_INTERACTION_PROFILE_STATE, 0, 0);
			XR10.xrGetCurrentInteractionProfile(session, toplevelUserPath, state);
			return state.interactionProfile();
		}
	}
	
	public static void restart() {
		restart(window, timeout, call);
	}
	
	public static void restart(GraphicWindow w, int timeout, callback call) {
		if(instance != null) {
			free();
		}
		connect(w, timeout, timestep, call);
	}
	
	public static void doPollEvents() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrEventDataBuffer edb = new XrEventDataBuffer(stack.calloc(4016));
			edb.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
			while(XR10.xrPollEvent(instance, edb) == XR10.XR_SUCCESS) {
				switch(edb.type()) {
					case XR10.XR_TYPE_EVENT_DATA_INSTANCE_LOSS_PENDING: {
						XrEventDataInstanceLossPending e = new XrEventDataInstanceLossPending(edb.varying());
						Tools.err("[VR]Instance loss pending! " + e.lossTime());
						break;
					}
					case XR10.XR_TYPE_EVENT_DATA_EVENTS_LOST: {
						XrEventDataEventsLost e = new XrEventDataEventsLost(edb.varying());
						Tools.err("[VR]Events lost!" + e.lostEventCount());
						break;
					}
					case XR10.XR_TYPE_EVENT_DATA_INTERACTION_PROFILE_CHANGED: {
						XrEventDataInteractionProfileChanged e = new XrEventDataInteractionProfileChanged(edb.varying());
						if(e.session() != session.address())
							Tools.err("[VR] XrEventDataInteractionProfileChanged for unknown Session!");
						break;
					}
					case XR10.XR_TYPE_EVENT_DATA_REFERENCE_SPACE_CHANGE_PENDING: {
						XrEventDataReferenceSpaceChangePending e = new XrEventDataReferenceSpaceChangePending(edb.varying());
						Tools.err("[VR] Reference space is changing! " + e.changeTime());
						break;
					}
					case XR10.XR_TYPE_EVENT_DATA_SESSION_STATE_CHANGED: {
						int type = edb.varying(8);
						call.stateChanged(type, 0);
						break;
					}
				}
				edb.clear();
				edb.type(XR10.XR_TYPE_EVENT_DATA_BUFFER);
			}
		}
	}
	public static long getSystemID() {
		return systemID;
	}
	public static XrInstance getInstance() {
		return instance;
	}
	public static XrSession getSession() {
		return session;
	}
	public static void setNear(float near) {
		VR.near = near;
	}
	public static void setFar(float far) {
		VR.far = far;
	}
	public static float getNear() {
		return near;
	}
	public static float getFar() {
		return far;
	}
	
	public static void createRenderPipeline() {
		
		temp = getViewConfigView(viewConfig);
		int amountViews = temp.capacity();
		
		xrViews = new XrView.Buffer(MemoryUtil.memCalloc(64*amountViews));
		try(MemoryStack stack = MemoryStack.stackPush()) {
			for(int i = 0; i < amountViews; i++) {
				XrView v = new XrView(stack.calloc(64));
				XrPosef pose = new XrPosef(stack.calloc(28));
				pose.orientation().set(0, 0, 0, 1);
				XrFovf fov = new XrFovf(stack.calloc(16));
				v.set(XR10.XR_TYPE_VIEW, 0, pose, fov);
				xrViews.put(i, v);
			}
		}
		
		
		long[] swapchainFormat = getSwapchainFormat();
		long format = swapchainFormat[0];
		for(long f : swapchainFormat) {
			if(f == GL33.GL_RGB10_A2) {
				format = f;
				break;
			}
			if(f == GL33.GL_RGBA16F) {
				format = f;
				break;
			}
			if(f == GL33.GL_SRGB8_ALPHA8) {
				format = f;
				break;
			}
			//Fallback
			if(f == GL33.GL_RGBA8) {
				format = f;
				break;
			}
			if(f == GL33.GL_RGBA8_SNORM) {
				format = f;
				break;
			}
		}
		VR.views = new view[amountViews];
		for(int i = 0; i < amountViews; i++) {
			VR.views[i] = new view();
			VR.views[i].format = format;
			VR.views[i].swapchain = createSwapchain(temp.get(i).recommendedImageRectWidth(), temp.get(i).recommendedImageRectHeight(), temp.get(i).recommendedSwapchainSampleCount(), format);
			VR.views[i].images = enumerateSwapchainImages(VR.views[i].swapchain);
			VR.views[i].configView = temp.get(i);
			VR.views[i].width = temp.get(i).recommendedImageRectWidth();
			VR.views[i].height = temp.get(i).recommendedImageRectHeight();
			VR.views[i].depthStencil = new FBOTexture(VR.views[i].width, VR.views[i].height, 1, false, GL33.GL_LINEAR, GL33.GL_LINEAR);
			VR.views[i].frameBuffer = GL33.glGenFramebuffers();
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, VR.views[i].frameBuffer);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_STENCIL_ATTACHMENT, GL33.GL_TEXTURE_2D, VR.views[i].depthStencil.getId(), 0);
		}
	}
	
	public static XrSpace createSpace(int type) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer pb = stack.callocPointer(48);
			XrReferenceSpaceCreateInfo info = new XrReferenceSpaceCreateInfo(stack.calloc(48));
			XrPosef pose = new XrPosef(stack.calloc(28));
			
			XrQuaternionf quat = new XrQuaternionf(stack.calloc(16));
			XrVector3f pos = new XrVector3f(stack.calloc(12));
			quat.x(0).y(0).z(0).w(1);
			
			pose.set(quat, pos);
			info.set(XR10.XR_TYPE_REFERENCE_SPACE_CREATE_INFO, 0, type, pose);
			int code = XR10.xrCreateReferenceSpace(session, info, pb);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to create reference space!" + code);
			return new XrSpace(pb.get(0), session);
		}
	}
	
	public static XrSpace createActionSpace(XrAction action, long subactionpath) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrActionSpaceCreateInfo space_ci = new XrActionSpaceCreateInfo(stack.calloc(64));
			XrPosef pose = new XrPosef(stack.calloc(28));
			pose.orientation().w(1);
			space_ci.set(XR10.XR_TYPE_ACTION_SPACE_CREATE_INFO, 0, action, subactionpath, pose);
			PointerBuffer pb = stack.callocPointer(1);
			int code = XR10.xrCreateActionSpace(session, space_ci, pb);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to create action space!" + code);
			return new XrSpace(pb.get(0), session);
		}
	}
	
	public static void render() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrFrameState frameState = waitFrame(stack);
			beginFrame(stack);
			
			XrCompositionLayerProjection layerProjection = new XrCompositionLayerProjection(stack.calloc(48));
			layerProjection.set(XR10.XR_TYPE_COMPOSITION_LAYER_PROJECTION, 0, XR10.XR_COMPOSITION_LAYER_BLEND_TEXTURE_SOURCE_ALPHA_BIT | XR10.XR_COMPOSITION_LAYER_CORRECT_CHROMATIC_ABERRATION_BIT, localSace, new XrCompositionLayerProjectionView.Buffer(stack.calloc(views.length*96)));
			
			
			PointerBuffer pointerBuffer = null;
			
			long currentFrameTime = frameState.predictedDisplayTime();
			
			{ //Actions
				XrActiveActionSet activeActionSet = new XrActiveActionSet(stack.calloc(16));
				activeActionSet.set(actionSet, 0);
				
				XrActionsSyncInfo sync_info = new XrActionsSyncInfo(stack.calloc(32));
				XrActiveActionSet.Buffer sets = new XrActiveActionSet.Buffer(stack.calloc(16*1));
				sets.put(0, activeActionSet);
				sync_info.set(XR10.XR_TYPE_ACTIONS_SYNC_INFO, 0, 1, sets);
				XR10.xrSyncActions(session, sync_info);
				
				XrActionStateGetInfo asget_info = new XrActionStateGetInfo(stack.calloc(32));
				for(action action : actions) {
					asget_info.set(XR10.XR_TYPE_ACTION_STATE_GET_INFO, 0, action.action, action.path);
					int status = 0;
					
					switch (action.type) {
						case XR_ACTION_TYPE_POSE_INPUT: {
							XrActionStatePose pose = new XrActionStatePose(stack.calloc(24));
							pose.set(XR10.XR_TYPE_ACTION_STATE_POSE, 0, false);
							status = XR10.xrGetActionStatePose(session, asget_info, pose);
							if(!XR10.XR_SUCCEEDED(status))
								throw new RuntimeException("Failed to get action state pose! " + status);
							if(action.isActive = pose.isActive()) {
								XrSpaceLocation sloc = new XrSpaceLocation(stack.calloc(56));
								XrPosef posef = new XrPosef(stack.calloc(28));
								sloc.set(XR10.XR_TYPE_SPACE_LOCATION, 0, 0, posef);
								status = XR10.xrLocateSpace(((actionPose)action).location, localSace, currentFrameTime, sloc);
								action.isValid = !XR10.XR_SUCCEEDED(status);
								XrPosef p = sloc.pose();
								if(((actionPose)action).positionValid = (sloc.locationFlags() & XR10.XR_SPACE_LOCATION_POSITION_VALID_BIT) != 0) {
									XrVector3f po = p.position$();
									((actionPose)action).position.set(po.x(), po.y(), po.z());
								}
								if(((actionPose)action).orientationValid = (sloc.locationFlags() & XR10.XR_SPACE_LOCATION_ORIENTATION_VALID_BIT) != 0) {
									XrQuaternionf ro = p.orientation();
									((actionPose)action).orientation.set(ro.x(), ro.y(), ro.z(), ro.w());
								}
							}
							break;
						}
						case XR_ACTION_TYPE_FLOAT_INPUT: {
							XrActionStateFloat f = new XrActionStateFloat(stack.calloc(XrActionStateFloat.SIZEOF));
							f.set(XR10.XR_TYPE_ACTION_STATE_FLOAT, 0, 0, false, 0, false);
							status = XR10.xrGetActionStateFloat(session, asget_info, new XrActionStateFloat(stack.calloc(XrActionStateFloat.SIZEOF)));
							action.isValid = !XR10.XR_SUCCEEDED(status);
							action.isActive = f.isActive();
							((actionFloat)action).value = f.currentState();
							break;
						}
						case XR_ACTION_TYPE_BOOLEAN_INPUT: {
							XrActionStateBoolean f = new XrActionStateBoolean(stack.calloc(XrActionStateBoolean.SIZEOF));
							f.set(XR10.XR_TYPE_ACTION_STATE_BOOLEAN, 0, false, false, 0, false);
							status = XR10.xrGetActionStateFloat(session, asget_info, new XrActionStateFloat(stack.calloc(XrActionStateFloat.SIZEOF)));
							action.isValid = !XR10.XR_SUCCEEDED(status);
							action.isActive = f.isActive();
							((actionBoolean)action).value = f.currentState();
							break;
						}
						case XR_ACTION_TYPE_VECTOR2F_INPUT: {
							XrActionStateVector2f f = new XrActionStateVector2f(stack.calloc(XrActionStateVector2f.SIZEOF));
							f.set(XR10.XR_TYPE_ACTION_STATE_VECTOR2F, 0, new XrVector2f(stack.calloc(XrVector2f.SIZEOF)), false, 0, false);
							status = XR10.xrGetActionStateVector2f(session, asget_info, f);
							action.isValid = !XR10.XR_SUCCEEDED(status);
							action.isActive = f.isActive();
							XrVector2f v = f.currentState();
							((actionVec2)action).value.set(v.x(), v.y());
							break;
						}
						case XR_ACTION_TYPE_VIBRATION_OUTPUT: {
							actionoVibr v = (actionoVibr) action;
							if(v.triggered) {
								beginHaptic(v.action, v.path, v.intensity, v.duration, v.frequency);
								v.triggered = false;
							}
							if(v.triggeredstop) {
								stopHaptic(v.action, v.path);
								v.triggeredstop = false;
							}
							break;
						}
						default:
							throw new IllegalArgumentException("Action not Found: " + action.type);
					}
					
					action.constructor.setValue(action);
				}
				
			}
			
			if(frameState.shouldRender()) {
				if(renderLayer(stack, currentFrameTime, layerProjection)) {
					pointerBuffer = stack.pointers(layerProjection.address());
				}
			}
				
			endFrame(stack, currentFrameTime, XR10.XR_ENVIRONMENT_BLEND_MODE_OPAQUE, pointerBuffer);
		}
	}
	private static Matrix4f viewMat = new Matrix4f();
	private static Matrix4f projMat = new Matrix4f();
	private static Quaternionf currentRotationInv = new Quaternionf();
	private static Vector3f currentPositionInv = new Vector3f();
	private static boolean renderLayer(MemoryStack stack, long currentFrameTime, XrCompositionLayerProjection layproj) {
		getViews(xrViews, currentFrameTime, localSace, views.length);
		XrCompositionLayerProjectionView.Buffer projectionLayerView_buffer = layproj.views();
		for(int i = 0; i < views.length; i++) {
			view view = views[i];
			int imageIndex = getImageIndex(view.swapchain);
			view.currentImage = imageIndex;
			//Waiting for image to write to
			XrSwapchainImageWaitInfo swait_info = new XrSwapchainImageWaitInfo(stack.calloc(24));
			swait_info.set(XR10.XR_TYPE_SWAPCHAIN_IMAGE_WAIT_INFO, 0, XR10.XR_INFINITE_DURATION);
			int code = XR10.xrWaitSwapchainImage(view.swapchain, swait_info);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to wait swapchainImage " + code);
			
			
			XrSwapchainSubImage subImage = new XrSwapchainSubImage(stack.calloc(32));
			{
				XrRect2Di rect = new XrRect2Di(stack.calloc(16));
				rect.set(new XrOffset2Di(stack.calloc(8)), new XrExtent2Di(stack.calloc(8)));
				rect.extent().width(view.width).height(view.height);
				subImage.set(view.swapchain, rect, 0);
			}
			//Setting view
			projectionLayerView_buffer.get(i).set(XR10.XR_TYPE_COMPOSITION_LAYER_PROJECTION_VIEW, 0, xrViews.get(i).pose(), xrViews.get(i).fov(), subImage);
			
			//Actual rendering
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, views[i].frameBuffer);
			GL33.glViewport(0, 0, views[i].width, views[i].height);
			GL33.glScissor(0, 0, views[i].width, views[i].height);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, view.images.get(imageIndex).image(), 0);
			GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
			
			XrPosef pose = xrViews.get(i).pose();
			XrVector3f position = pose.position$();
			XrQuaternionf rotation = pose.orientation();
			currentRotationInv.set(rotation.x(), rotation.y(), rotation.z(), rotation.w()).conjugate();
			currentPositionInv.set(-position.x(), -position.y(), -position.z());
			viewMat.identity();
			viewMat.rotate(currentRotationInv).translate(currentPositionInv);
			XrFovf fov = xrViews.get(i).fov();
			projMat.identity();
			projMat.perspectiveOffCenterFov(fov.angleLeft(), fov.angleRight(), fov.angleDown(), fov.angleUp(), near, far);
			call.render(i, views[i].frameBuffer, views[i].width, views[i].height, (int) views[i].format, viewMat, projMat);
			
			//Releasing image
			XrSwapchainImageReleaseInfo releaseInfo = new XrSwapchainImageReleaseInfo(stack.calloc(16));
			releaseInfo.set(XR10.XR_TYPE_SWAPCHAIN_IMAGE_RELEASE_INFO, 0);
			code = XR10.xrReleaseSwapchainImage(view.swapchain, releaseInfo);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to release swapchain Image! " + code);
		}
		layproj.views(projectionLayerView_buffer);
		return true;
	}
	
	public static int getImageIndex(XrSwapchain swapchain) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrSwapchainImageAcquireInfo info = new XrSwapchainImageAcquireInfo(stack.calloc(16));
			info.set(XR10.XR_TYPE_SWAPCHAIN_IMAGE_ACQUIRE_INFO, 0);
			IntBuffer intbuffer = stack.callocInt(1);
			int code = XR10.xrAcquireSwapchainImage(swapchain, info, intbuffer);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to acquire swapchain image! " + code);
			return intbuffer.get(0);
		}
	}
	
	public static XrFrameState waitFrame(MemoryStack stack) {
		XrFrameState frameState = new XrFrameState(stack.calloc(40));
		frameState.set(XR10.XR_TYPE_FRAME_STATE, 0, 0, 0, false);
		XrFrameWaitInfo framewait_info = new XrFrameWaitInfo(stack.calloc(16));
		framewait_info.set(XR10.XR_TYPE_FRAME_WAIT_INFO, 0);
		int code = XR10.xrWaitFrame(session, framewait_info, frameState);
		if(!XR10.XR_SUCCEEDED(code))
			throw new RuntimeException("Failed to wait frame!" + code);
		return frameState;
	}
	
	public static void beginFrame(MemoryStack stack) {
		XrFrameBeginInfo framebegin_info = new XrFrameBeginInfo(stack.calloc(16));
		framebegin_info.set(XR10.XR_TYPE_FRAME_BEGIN_INFO, 0);
		int code = XR10.xrBeginFrame(session, framebegin_info);
		if(!XR10.XR_SUCCEEDED(code))
			throw new RuntimeException("Failed to begin Frame!" + code);
	}
	public static void endFrame(MemoryStack stack, long displayTime, int blendMode, PointerBuffer layers) {
		XrFrameEndInfo frameend_info = new XrFrameEndInfo(stack.calloc(40));
		frameend_info.set(XR10.XR_TYPE_FRAME_END_INFO, 0, displayTime, blendMode, layers == null ? 0 : layers.remaining(), layers);
		int code = XR10.xrEndFrame(session, frameend_info);
		if(!XR10.XR_SUCCEEDED(code))
			throw new RuntimeException("Failed to end Frame!" + code);
	}
	
	
	public static XrView.Buffer getViews(XrView.Buffer buff, long predictedDisplayTime, XrSpace space, int amount) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrViewState viewState = new XrViewState(stack.calloc(24));
			viewState.set(XR10.XR_TYPE_VIEW_STATE, 0, 0);
			XrViewLocateInfo info = new XrViewLocateInfo(stack.calloc(40));
			info.set(XR10.XR_TYPE_VIEW_LOCATE_INFO, 0, viewConfig, predictedDisplayTime, space);
			IntBuffer intbuffer = stack.callocInt(1);
			int code = XR10.xrLocateViews(session, info, viewState, intbuffer, buff);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to get Views! " + code);
			return buff;
		}
		
	}
	
	public static void beginHaptic(XrAction action, long subpath, float amplitude, long duration, float frequency) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrHapticActionInfo haptic_info = new XrHapticActionInfo(stack.calloc(32));
			haptic_info.set(XR10.XR_TYPE_HAPTIC_ACTION_INFO, 0, action, subpath);
			XrHapticVibration vibration = new XrHapticVibration(stack.calloc(XrHapticVibration.SIZEOF));
			vibration.set(XR10.XR_TYPE_HAPTIC_VIBRATION, 0, duration, frequency, amplitude);
			int code = XR10.xrApplyHapticFeedback(session, haptic_info, new XrHapticBaseHeader(MemoryUtil.memByteBuffer(vibration.address(), XrHapticVibration.SIZEOF)));
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to start haptics! " + code);
		}
	}
	
	public static void stopHaptic(XrAction action, long subpath) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			XrHapticActionInfo haptic_info = new XrHapticActionInfo(stack.calloc(1));
			haptic_info.set(XR10.XR_TYPE_HAPTIC_ACTION_INFO, 0, action, subpath);
			int code = XR10.xrStopHapticFeedback(session, haptic_info);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to stop haptics! " + code);
		}
	}
	
	public static int[] getViewConfig() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer intbuffer = stack.callocInt(1);
			int codea = XR10.xrEnumerateViewConfigurations(instance, systemID, intbuffer, null);
			IntBuffer viewconfigs = stack.callocInt(intbuffer.get(0));
			int codeb = XR10.xrEnumerateViewConfigurations(instance, systemID, intbuffer, viewconfigs);
			if(!XR10.XR_SUCCEEDED(codea) || !XR10.XR_SUCCEEDED(codeb))
				throw new RuntimeException("Failed to get Configuration! " + codea + " || " + codeb);
			if(intbuffer.get(0) == 0)
				throw new RuntimeException("No Configuration found!");
			int[] configuration = new int[intbuffer.get(0)];
			for(int i = 0; i < intbuffer.capacity(); i++)
				configuration[i] = viewconfigs.get(i);
			VR.viewConfig = configuration[0]; //Get first configuration
			return configuration;
		}
		
	}
	
	public static XrViewConfigurationView.Buffer getViewConfigView(int configuration) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer intbuffer = stack.callocInt(1);
			int codea = XR10.xrEnumerateViewConfigurationViews(instance, systemID, 2, intbuffer, null);
			XrViewConfigurationView.Buffer views = new XrViewConfigurationView.Buffer(MemoryUtil.memCalloc(intbuffer.get(0)*40));
			for(int i = 0; i < intbuffer.get(0); i++) {
				XrViewConfigurationView c = new XrViewConfigurationView(stack.calloc(40));
				c.set(XR10.XR_TYPE_VIEW_CONFIGURATION_VIEW, 0, 0, 0, 0, 0, 0, 0);
				views.put(i, c);
			}
			int codeb = XR10.xrEnumerateViewConfigurationViews(instance, systemID, configuration, intbuffer, views);
			if(!XR10.XR_SUCCEEDED(codea) || !XR10.XR_SUCCEEDED(codeb))
				throw new RuntimeException("Failed to get Configuration! " + codea + " || " + codeb);
			if(intbuffer.get(0) == 0)
				throw new RuntimeException("No Configuration found!");
			return views;
		}
		
	}
	
	public static long[] getSwapchainFormat() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer intbuffer = stack.callocInt(1);
			int codea = XR10.xrEnumerateSwapchainFormats(session, intbuffer, null);
			LongBuffer viewconfigs = stack.callocLong(intbuffer.get(0));
			int codeb = XR10.xrEnumerateSwapchainFormats(session, intbuffer, viewconfigs);
			if(!XR10.XR_SUCCEEDED(codea) || !XR10.XR_SUCCEEDED(codeb))
				throw new RuntimeException("Failed to get Swapchain Format!! " + codea + " || " + codeb);
			if(intbuffer.get(0) == 0)
				throw new RuntimeException("No Swapchain Format found!");
			long[] swapchainFormat = new long[viewconfigs.capacity()];
			for(int i = 0; i < viewconfigs.capacity(); i++)
				swapchainFormat[i] = viewconfigs.get(i);
			return swapchainFormat;
		}
	}
	
	public static XrSwapchain createSwapchain(int width, int height, int samples, long swapchainFormat) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			//Color
			XrSwapchainCreateInfo swapchain_col_ci = new XrSwapchainCreateInfo(stack.calloc(64));
			swapchain_col_ci.set(XR10.XR_TYPE_SWAPCHAIN_CREATE_INFO, 0, XR10.XR_SWAPCHAIN_USAGE_SAMPLED_BIT | XR10.XR_SWAPCHAIN_USAGE_COLOR_ATTACHMENT_BIT, 0, swapchainFormat, samples, width, height, 1, 1, 1);
			PointerBuffer pointerbuffer = stack.callocPointer(1);
			int code = XR10.xrCreateSwapchain(session, swapchain_col_ci, pointerbuffer);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to create Color Swapchain! " + code);
			return new XrSwapchain(pointerbuffer.get(0), session);
		}
	}
	
	public static XrSwapchainImageOpenGLKHR.Buffer enumerateSwapchainImages(XrSwapchain swapchain) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer intbuffer = stack.callocInt(1);
			int codea = XR10.xrEnumerateSwapchainImages(swapchain, intbuffer, null);
			int size = intbuffer.get(0);
			XrSwapchainImageOpenGLKHR.Buffer swapchainImages = new XrSwapchainImageOpenGLKHR.Buffer(MemoryUtil.memCalloc(size*24));
			
			for(int i = 0; i < size; i++) {
				XrSwapchainImageOpenGLKHR img = new XrSwapchainImageOpenGLKHR(stack.calloc(24));
				img.set(KHROpenGLEnable.XR_TYPE_SWAPCHAIN_IMAGE_OPENGL_KHR, 0);
				swapchainImages.put(i, img);
			}
			
			int codeb = XR10.xrEnumerateSwapchainImages(swapchain, intbuffer, XrSwapchainImageBaseHeader.create(swapchainImages.address(), swapchainImages.capacity()));
					
			if(!XR10.XR_SUCCEEDED(codea) || !XR10.XR_SUCCEEDED(codeb))
				throw new RuntimeException("Failed to get Swapchain Images! " + codea + " || " + codeb);
			if(intbuffer.get(0) == 0)
				throw new RuntimeException("No Swapchain Images found!");
			return swapchainImages;
		}
		
	}
	
	
	public static void beginSession() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			attachActionSet(actionSet);
			XrSessionBeginInfo begin_info = new XrSessionBeginInfo(stack.calloc(24));
			begin_info.set(XR10.XR_TYPE_SESSION_BEGIN_INFO, 0, viewConfig);
			int code = XR10.xrBeginSession(session, begin_info);
			if(!XR10.XR_SUCCEEDED(code))
				throw new RuntimeException("Failed to begin Session! " + code);
			sessionRunning = true;
		}
	
	}
	
	public static void endSession() {
		int code = XR10.xrEndSession(session);
		if(!XR10.XR_SUCCEEDED(code))
			throw new RuntimeException("Failed to end session! " + code);
		sessionRunning = false;
	}
	
	public static boolean isSessionRunning() {
		return sessionRunning;
	}
	
	public static boolean wantsExit() {
		return wantsExit;
	}
	
	public static void free() {
		if(xrViews != null) { //If Renderpipeline has been created
			xrViews.free();
			temp.free();
			for(view v : views) {
				v.images.free();
				GL33.glDeleteTextures(v.depthStencil.getId());
				GL33.glDeleteFramebuffers(v.frameBuffer);
				XR10.xrDestroySwapchain(v.swapchain);
			}
		}
		XR10.xrDestroySession(session);
		XR10.xrDestroySpace(localSace);
		XR10.xrDestroyActionSet(actionSet);
		XR10.xrDestroyInstance(instance);
		instance = null;
		sessionRunning = false;
		isConnected = false;
	}

	public static boolean isConnected() {
		return isConnected;
	}

	public static XrSpace getLocalSace() {
		return localSace;
	}

	public static GraphicWindow getWindow() {
		return window;
	}

	public static callback getCall() {
		return call;
	}

	public static void setCall(callback call) {
		VR.call = call;
	}

	public static view[] getViews() {
		return views;
	}

	public static XrView.Buffer getXrViews() {
		return xrViews;
	}

	public static Matrix4f getViewMat() {
		return viewMat;
	}

	public static void setViewMat(Matrix4f viewMat) {
		VR.viewMat = viewMat;
	}

	public static Matrix4f getProjMat() {
		return projMat;
	}

	public static void setProjMat(Matrix4f projMat) {
		VR.projMat = projMat;
	}

	public static void setSystemID(long systemID) {
		VR.systemID = systemID;
	}

	public static void setViewConfig(int viewConfig) {
		VR.viewConfig = viewConfig;
	}
	
	public static List<action> getActions() {
		return actions;
	}
	
	public static XrActionSet getActionSet() {
		return actionSet;
	}
	public static Vector3f getCurrentPositionInv() {
		return currentPositionInv;
	}
	public static Quaternionf getCurrentRotationInv() {
		return currentRotationInv;
	}

}
