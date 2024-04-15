package Kartoffel.Licht.Input;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_RAW_MOUSE_MOTION;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwRawMouseMotionSupported;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import Kartoffel.Licht.Rendering.GraphicWindow;

public class CursorCallback extends GLFWCursorPosCallback implements InputCall{
	
	private GraphicWindow renderer;
	
	private int motionX = 0, motionY = 0;
	private int preX = 0, preY = 0, X = 0, Y = 0;
	private int rpreX = 0, rpreY = 0, rX, rY, rmotionX = 0, rmotionY = 0;
	private static long[] cursors = new long[14];
	
	public byte r_cursor = 0;
	public boolean r_gaming = false;
	
	public void createCursors() {
		cursors[0] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		cursors[1] = 0;
		cursors[2] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
		cursors[3] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
		cursors[4] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		cursors[5] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
		cursors[6] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR);
		cursors[7] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
		cursors[8] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
		cursors[9] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_EW_CURSOR);
		cursors[10] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR);
		cursors[11] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NS_CURSOR);
		cursors[12] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR);
		cursors[13] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
	}
	
	public CursorCallback(GraphicWindow renderer) {
		this.renderer = renderer;
	}
	
	public GraphicWindow hideCursor(boolean b) {
		if(b)
			glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		else
			glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		return renderer;
	}
	
	
	/**
	 *  0 = default<br>
	 *  1 = hidden<br>
	 *  2 = Crosshair<br>
	 *  3 = Hand<br>
	 *  4 = resize horizontal<br>
	 *  5 = writing<br>
	 *  6 = not allowed<br>
	 *  7 = Hand<br>
	 *  8 = move<br>
	 *  9 = resize horizontal<br>
	 *  10 = resize top-right/bottom-left<br>
	 *  11 = resize vertical<br>
	 *  12 = resize top-left/bottom-right<br>
	 *  13 = resize vertical<br>
	 *  (13)
	 * 
	 * @param cursor
	 */
	public void setCursor(byte cursor) {
		if(r_cursor != cursor) {			r_cursor = cursor;
			if(cursor == 1)
				glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
			else if(cursor >= 0 && cursor <= 13) {
				GLFW.glfwSetCursor(renderer.getWINDOW_ID(), cursors[cursor]);
				glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			}		}
	}
	public GraphicWindow setGamingCursor(boolean b) {
		if(r_gaming != b) {
			if(b)
				glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			else
				glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			
			if (glfwRawMouseMotionSupported())
			    glfwSetInputMode(renderer.getWINDOW_ID(), GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
			r_gaming = b;
		}
		return renderer;
	}


	@Override
	public void invoke(long window, double xpos, double ypos) {
		X = (int) xpos;
		Y = (int) ypos;
		rX = renderer.getPositionX();
		rY = renderer.getPositionY();
	}
	
	public int getMotionX() {
		return motionX;
	}
	
	public int getMotionY() {
		return motionY;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	public int getRmotionX() {
		return rmotionX;
	}
	
	public int getRmotionY() {
		return rmotionY;
	}
	
	public boolean isGaming() {
		return r_gaming;
	}
	
	public byte getCursor() {
		return r_cursor;
	}

	@Override
	public void update() {
		motionX = preX - X;
		motionY = preY - Y;
		preX = X;
		preY = Y;
		rmotionX = motionX-(rX-rpreX);
		rmotionY = motionY-(rY-rpreY);
		rpreX = rX;
		rpreY = rY;
	}
	
	
	
	
}
