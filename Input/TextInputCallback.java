package Kartoffel.Licht.Input;

import org.lwjgl.glfw.GLFWCharCallback;

public class TextInputCallback extends GLFWCharCallback implements InputCall{
	
	private TextInputInstance currentInstance;
	
	@Override
	public void invoke(long window, int codepoint) {
		if(currentInstance != null)
			currentInstance.invoke(codepoint);
	}
	
	public TextInputInstance getCurrentInstance() {
		return currentInstance;
	}
	
	public void setCurrentInstance(TextInputInstance currentInstance) {
		this.currentInstance = currentInstance;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
}