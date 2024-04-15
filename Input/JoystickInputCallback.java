package Kartoffel.Licht.Input;

import static Kartoffel.Licht.Tools.Tools.conm;
import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadName;
import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import static org.lwjgl.glfw.GLFW.glfwGetJoystickName;
import static org.lwjgl.glfw.GLFW.glfwJoystickIsGamepad;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

public class JoystickInputCallback implements GLFWJoystickCallbackI, InputCall{

	 public static final int
     GLFW_GAMEPAD_BUTTON_A            = 0,
     GLFW_GAMEPAD_BUTTON_B            = 1,
     GLFW_GAMEPAD_BUTTON_X            = 2,
     GLFW_GAMEPAD_BUTTON_Y            = 3,
     GLFW_GAMEPAD_BUTTON_LEFT_BUMPER  = 4,
     GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5,
     GLFW_GAMEPAD_BUTTON_BACK         = 6,
     GLFW_GAMEPAD_BUTTON_START        = 7,
     GLFW_GAMEPAD_BUTTON_GUIDE        = 8,
     GLFW_GAMEPAD_BUTTON_LEFT_THUMB   = 9,
     GLFW_GAMEPAD_BUTTON_RIGHT_THUMB  = 10,
     GLFW_GAMEPAD_BUTTON_DPAD_UP      = 11,
     GLFW_GAMEPAD_BUTTON_DPAD_RIGHT   = 12,
     GLFW_GAMEPAD_BUTTON_DPAD_DOWN    = 13,
     GLFW_GAMEPAD_BUTTON_DPAD_LEFT    = 14,
     GLFW_GAMEPAD_BUTTON_LAST         = GLFW_GAMEPAD_BUTTON_DPAD_LEFT,
     GLFW_GAMEPAD_BUTTON_CROSS        = GLFW_GAMEPAD_BUTTON_A,
     GLFW_GAMEPAD_BUTTON_CIRCLE       = GLFW_GAMEPAD_BUTTON_B,
     GLFW_GAMEPAD_BUTTON_SQUARE       = GLFW_GAMEPAD_BUTTON_X,
     GLFW_GAMEPAD_BUTTON_TRIANGLE     = GLFW_GAMEPAD_BUTTON_Y;
	 
	 public static final int
     GLFW_GAMEPAD_AXIS_LEFT_X        = 0,
     GLFW_GAMEPAD_AXIS_LEFT_Y        = 1,
     GLFW_GAMEPAD_AXIS_RIGHT_X       = 2,
     GLFW_GAMEPAD_AXIS_RIGHT_Y       = 3,
     GLFW_GAMEPAD_AXIS_LEFT_TRIGGER  = 4,
     GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER = 5;
	
	 private List<Integer> controllers = new ArrayList<Integer>();
	
	@Override
	public void invoke(int jid, int event) {
		if(event == GLFW_CONNECTED) {
			conm("Joystick connected!: " + glfwGetJoystickName(jid));
			if(glfwJoystickIsGamepad(jid)) {
				conm("Joystick is a gamepad: " + glfwGetGamepadName(jid));
				controllers.add(jid);
				
			}
		}
		if(event == GLFW_DISCONNECTED) {
			conm("Joystick disconnected");
			controllers.remove((Object)jid);
		}
	}
	
	public boolean getButton(int i, int jid) {
		GLFWGamepadState state = GLFWGamepadState.create();
		 
		if (glfwGetGamepadState(jid, state))
		{
		    if (state.buttons(i) == GLFW_PRESS)
		    {
		    	return true;
		    }
		    else {
		    	return false;
		    }
		    
		    
		}else {
			controllers.remove((Object)jid);
		}
		return false;
	}
	
	public float getStick(int i, int jid) {
		GLFWGamepadState state = GLFWGamepadState.create();
		 
		if (glfwGetGamepadState(jid, state))
		{
			float ax = state.axes(i);
			if(i == 4 || i == 5) {
				if(ax < 0)
					ax /= 10;
				ax += 0.1f;
				ax /= 1.1f;
			}
		    return ax;
		}else {
			controllers.remove((Object)jid);
		}
		return 0;
	} 
	
	public List<Integer> getControllers() {
		return controllers;
	}

	@Override
	public void update() {
		
	}

}
