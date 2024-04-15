package Kartoffel.Licht.Engine;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Geo.LibBulletJme.Physics;
import Kartoffel.Licht.Media.Audio;
import Kartoffel.Licht.Media.VR;
import Kartoffel.Licht.Media.VR.action;
import Kartoffel.Licht.Media.VR.actionBindingsConstructor;
import Kartoffel.Licht.Media.VR.actionConstructor;
import Kartoffel.Licht.Media.VR.actionFamilyConstructor;
import Kartoffel.Licht.Media.VR.actionPose;
import Kartoffel.Licht.Media.VR.callback;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Shaders.PostShader;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Tools.FileDialog;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;
import Kartoffel.Licht.Tools.Tools.every;

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
public abstract class JAppVR extends JApp{
	
	private PostShader ps_pass;
	
	//Vr
	protected Vector3f vr_hand_left_pos = new Vector3f();
	protected Quaternionf vr_hand_left_orientation = new Quaternionf();
	protected Vector3f vr_hand_right_pos = new Vector3f();
	protected Quaternionf vr_hand_right_orientation = new Quaternionf();
	
	protected int vr_framebuffer;
	protected int vr_width;
	protected int vr_height;
	protected int referenceView = 0;
	
	protected Vector3f vr_scale = new Vector3f(5);
	protected Vector3f vr_cam_position = new Vector3f();
	protected Vector3f vr_cam_dir = new Vector3f();
	
	public class VRCamera extends Camera{
		
	}
	
	
	
	
	/**
	 * Runs the application
	 * @param save if the main loop should should catch any Throwable and directly continue with the next cycle (setting this to true is a bad habit)
	 * @throws Exception if any exception occurs
	 */
	final public void run(boolean save) throws Exception{
		initi(save);
		ps_pass = new PostShader(PostShader.POST_SHADER_PASS);
		Tools.every fps = new Tools.every();
		Tools.every tps = new Tools.every();
		loop(fps, tps, save);
		endt();
	}
	@Override
	protected void loop(every fps, every tps, boolean save) {

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
						if(VR.isConnected())
							VR.doPollEvents();
						if(VR.isSessionRunning()) {
							VR.render();
							if(framebuffer == null) {
								GL33.glViewport(0, 0, window.getWidth(), window.getHeight());
								GL33.glBindFramebuffer(GL_FRAMEBUFFER, 0); 
							}
							else {
								framebuffer.clear();
							}
							ps_pass.render(new Texture(VR.getViews()[referenceView].getCurrentImage()));
						}else
							paintAll();
						window.updateWindow();
						clearBuffers();
						if(!VR.isConnected())
							camera.update();
						else {
							VR.setFar(camera.getProjectionBox().getZ_FAR());
							VR.setNear(camera.getProjectionBox().getZ_NEAR());
							//Camera gets updated in render (Eye) method
						}
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
	
	public void connectToVr() {
		if(VR.isConnected())
			return;
		VR.setProjMat(camera.getProjection());
		VR.connect(window, 5, 100, new callback() {
			
			@Override
			public void stateChanged(int stateChange, long time) {
				if(stateChange == VR.XR_SESSION_STATE_READY) {
					VR.getViewConfig();
					VR.beginSession();
					VR.createRenderPipeline();
				}
			}
			Vector3f temp = new Vector3f();
			@Override
			public void render(int index, int frameBuffer, int width, int height, int format, Matrix4f viewMat, Matrix4f projMat) {
				temp.add(-VR.getCurrentPositionInv().x/VR.getViews().length, -VR.getCurrentPositionInv().y/VR.getViews().length, -VR.getCurrentPositionInv().z/VR.getViews().length);
				if(index == VR.getViews().length-1) {
					vr_cam_position.set(temp).mul(vr_scale);
					vr_cam_dir.set(viewMat.get(0, 2), viewMat.get(1, 2), viewMat.get(2, 2));
					temp.set(0);
				}
				try {
					vr_framebuffer = frameBuffer;
					vr_width = width;
					vr_height = height;
					camera.getViewMatrixNew(camera.getViewMatrix());
					viewMat.m30(viewMat.m30()*vr_scale.x).m31(viewMat.m31()*vr_scale.y).m32(viewMat.m32()*vr_scale.z);
					viewMat.mul(camera.getViewMatrix(), camera.getViewMatrix());
					camera.update(false, false);
					paintAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public actionFamilyConstructor getActions() {
				actionFamilyConstructor f = new actionFamilyConstructor();
				actionConstructor poseLeft = new actionConstructor("pose_left", "Left Hand", VR.XR_ACTION_TYPE_POSE_INPUT, VR.SP_HAND_LEFT, VR.SP_HAND_LEFT+VR.NA_IN_GRIP_POSE) {
					
					@Override
					public void setValue(action action) {
						vr_hand_left_pos.set(((actionPose)action).position).mul(vr_scale);
						vr_hand_left_orientation.set(((actionPose)action).orientation);
					}
				};
				actionConstructor poseRight = new actionConstructor("pose_right", "Position Right", VR.XR_ACTION_TYPE_POSE_INPUT, VR.SP_HAND_RIGHT, VR.SP_HAND_RIGHT+VR.NA_IN_GRIP_POSE) {
					
					@Override
					public void setValue(action action) {
						vr_hand_right_pos.set(((actionPose)action).position).mul(vr_scale);
						vr_hand_right_orientation.set(((actionPose)action).orientation);
					}
				};
				
				f.actions.add(poseLeft);
				f.actions.add(poseRight);
				f.actionBindings.add(new actionBindingsConstructor(VR.INTERACTPROF_DEFAULT, poseLeft, poseRight));
				return getActionsExt(f);
			}
		});
	}
	/**
	 * Performs any edits of the submitted action tree
	 * @param constr the object
	 * @return the object, may be modified
	 */
	protected actionFamilyConstructor getActionsExt(actionFamilyConstructor constr) {
		return constr;
	}
	
	/**
	 * Binds the Frame Buffer all geometry should be rendered to
	 */
	protected void bindMainBuffer() {
		if(VR.isSessionRunning()) {
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, vr_framebuffer);
			GL33.glViewport(0, 0, vr_width, vr_height);
			GL33.glScissor(0, 0, vr_width, vr_height);
			return;
		}
		if(framebuffer == null) {
			GL33.glViewport(0, 0, window.getWidth(), window.getHeight());
			GL33.glBindFramebuffer(GL_FRAMEBUFFER, 0); 
		}
		else {
			framebuffer.clear();
		}
	};
	
	public Vector3f getVr_cam_dir() {
		return vr_cam_dir;
	}
	
	public Vector3f getVr_cam_position() {
		return vr_cam_position;
	}
	
	public Quaternionf getVr_hand_left_orientation() {
		return vr_hand_left_orientation;
	}
	
	public Vector3f getVr_hand_left_pos() {
		return vr_hand_left_pos;
	}
	
	public Quaternionf getVr_hand_right_orientation() {
		return vr_hand_right_orientation;
	}
	
	public Vector3f getVr_hand_right_pos() {
		return vr_hand_right_pos;
	}
	
	public Vector3f getVr_scale() {
		return vr_scale;
	}
	
	public void setVr_scale(Vector3f vr_scale) {
		this.vr_scale = vr_scale;
	}
	

}
