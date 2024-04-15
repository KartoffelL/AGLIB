package Kartoffel.Licht.JGL;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import Kartoffel.Licht.Input.CursorCallback;
import Kartoffel.Licht.JGL.Components.JGLBackground;
import Kartoffel.Licht.JGL.Components.JGLBar;
import Kartoffel.Licht.JGL.Components.JGLButton;
import Kartoffel.Licht.JGL.Components.JGLPSquare;
import Kartoffel.Licht.JGL.Components.JGLSlider;
import Kartoffel.Licht.JGL.Components.JGLSlider2D;
import Kartoffel.Licht.JGL.Components.JGLSquare;
import Kartoffel.Licht.JGL.Components.JGLTable;
import Kartoffel.Licht.JGL.Components.JGLTest;
import Kartoffel.Licht.JGL.Components.JGLTextField;
import Kartoffel.Licht.JGL.Components.JGLTextInput;
import Kartoffel.Licht.Java.Rectangle;
import Kartoffel.Licht.Rendering.GEntity;

/**
 * A JGLComponent, which can be added to a JGLFrame. Not really optimized, but working.<br>
 * A JGLComponent has bounds, which are non dependent of the Window Size, and many states<br>
 * the JGLComponent can be in, examples:<br>
 * <br>
 * isVisible - if the Component is processed<br>
 * isFocus - if the Component is focused<br>
 * isHovered - if the Component is Hovered<br>
 * isClicking - if the Component is clicked at<br>
 * isRClicking - right-click variant<br>
 * ignoreCursor - if the Cursor is affected by this Component<br>
 *
 */
public class JGLComponent {

	protected Rectangle bounds;
	protected int bx = -1, by = -1;
	
	protected List<JGLComponent> components = new ArrayList<>();
	protected JGLComponent hover_Element = null;
	protected JGLComponent rightClick_Element;
	
	protected boolean isPaintedReverse = false;
	protected boolean isVisible = true;
	protected boolean isHovered = false;
	protected boolean isFocused = false;
	protected boolean isDragging = false;
	protected boolean ignoreCursor = false;
	protected boolean isClicking[] = new boolean[3];
	protected boolean wasClicking[] = new boolean[3];
	
	protected byte cursor = -1;
	
	public JGLComponent(int x, int y, int w, int h) {
		this.bounds = new Rectangle(x, y, w, h);
	}
	
	//Events
	private boolean lastMove = false;
	
	private boolean carrying = true;
	
	final void paint(int xoff, int yoff, JGLFI info, boolean hovered) {
		if(!isVisible)
			return;
		//Pre-variables
		isHovered = isInBounds(info) && hovered;
		if(isFocused)
			info.hit = true;
		//Custom cursor
		if(!ignoreCursor && isHovered && cursor != -1) {
			info.cursor = cursor;
		}
		//Painting
		if(!isPaintedReverse)
			for(JGLComponent c : components) {
				c.paint(xoff+(carrying ? bounds.x : 0), yoff+(carrying ? bounds.y : 0), info, hovered);
			}
		else
			if(components.size() != 0)
				for(int i = components.size()-1; i > 0; i--) {
					components.get(i).paint(xoff+(carrying ? bounds.x : 0), yoff+(carrying ? bounds.y : 0), info, hovered);
				}
		this.paintComponent(info, xoff, yoff);
		//Entering/Exiting
		if(isHovered != lastMove)
			MouseEnterComponent(info, isHovered);
		lastMove = isHovered;
		//Hover
		if(hover_Element != null) {
			if(isHovered) {
				hover_Element.setVisible(true);
				hover_Element.getBounds().x = info.mx;
				hover_Element.getBounds().y = info.my;
			}
			else
				hover_Element.setVisible(false);
		}
		//Clicking
		for(int i = 0; i < 3; i++) {
			isClicking[i] = info.window.getCallback_Key().isMouseButtonDown(i);
			if((isClicking[i] != wasClicking[i]))
				MouseClickComponent(info, getBindedX(info), getBindedY(info), i, isClicking[i], isFocused = isHovered);
			wasClicking[i] = isClicking[i];
		}
		//Moving
		if(isHovered || isDragging)
			if(info.window.getCallback_Cursor().getMotionX() != 0 || info.window.getCallback_Cursor().getMotionY() != 0) {
					MouseMoveComponent(info, getBindedX(info), getBindedY(info), (lastMove == false && isHovered == true) ? 1 : (lastMove == true && isHovered == false) ? -1 : 0);
				if(isDragging = isClicking[0])
					DragComponent(info, getBindedX(info), getBindedY(info), info.window.getCallback_Cursor().getMotionX(), info.window.getCallback_Cursor().getMotionY());
				
			}
				
	}
	final public void move(int x, int y) {
		for(JGLComponent c : components)
			c.move(x, y);
		this.bounds.x += x;
		this.bounds.y += y;
	}
	final public void pack() {
		for(JGLComponent c : components)
			c.pack();
		ReboundComponent(bounds);
	}
	
	final public void getAllChildComponents(List<JGLComponent> list) {
		for(JGLComponent c : components)
			c.getAllChildComponents(list);
		list.addAll(components);
		list.add(this);
	}
	
	
	
	
	///////
	//Component Events
	protected void paintComponent(JGLFI info, int xoff, int yoff) {}
	
	protected void disposeComponent() {}
	
	protected void DragComponent(JGLFI info,int mx, int my, int x, int y) {}
	
	protected void MouseMoveComponent(JGLFI info, int ax, int ay, int mode) {}
	
	protected void MouseEnterComponent(JGLFI info, boolean enter) {}
	
	protected void MouseClickComponent(JGLFI info, int x, int y, int button, boolean down, boolean inBounds) {}
	
	protected void ReboundComponent(Rectangle bounds) {}
	
//	protected void MouseInspectComponent(JGLFI info, int x, int y) { //TODO
//		if(rightClick_Element != null) {
//			rightClick_Element.getBounds().x = x;
//			rightClick_Element.getBounds().y = y;
//			rightClick_Element.setBindings(bx, by);
//			rightClick_Element.setVisible(true);
//		}
//	}
	////////////////////
	
	final void dispose() {
		for(JGLComponent c : components)
			c.dispose();
		disposeComponent();
	}
	
	final public JGLComponent add(JGLComponent c) {
		if(c != this)
			if(!components.contains(c))
				components.add(c);
		return this;
	}
	int x;
	int y;
	final protected GEntity place(GEntity gent, JGLFI info) {
		return place(gent, info, 0, 0);
	}
	final protected Vector4f place(Vector4f vec, JGLFI info) {
		return place(vec, info, 0, 0);
	}
	final protected GEntity place(GEntity gent, JGLFI info, int xoff, int yoff) {
		x = (getBounds().x+xoff)+getBounds().width/2;
		y = (getBounds().y+yoff)+getBounds().height/2;
		gent.getPosition().x = ((float)(x)/info.sx*2+bx);
		gent.getPosition().y = -((float)(y)/info.sy*2+by);
		gent.getPosition().z = 0;
		gent.getScale().x = (float)(getBounds().width*2)/info.sx;
		gent.getScale().y = (float)(getBounds().height*2)/info.sy;
		return gent;
	}
	final protected Vector4f place(Vector4f vec, JGLFI info, int xoff, int yoff) {
		x = (getBounds().x+xoff)+getBounds().width/2;
		y =(getBounds().y+yoff)+getBounds().height/2;
		vec.x = ((float)(x)/info.sx*2+bx);
		vec.y = -((float)(y)/info.sy*2+by);
		vec.z = (float)(getBounds().width*2)/info.sx;
		vec.w = (float)(getBounds().height*2)/info.sy;
		return vec;
	}
	/**
	 * Converts the mouse X position to the correctly bound value
	 */
	final private int getBindedX(JGLFI info) {
		CursorCallback c = info.window.getCallback_Cursor();
		//return bx == 1 ? info.sx-c.getX() : bx == 0 ? c.getX()-info.sx/2+getBounds().width : c.getX();
		return (int) (  c.getX()-info.sx*((bx+1.0)/2));
	}
	/**
	 * Converts the mouse Y position to the correctly bound value
	 */
	final private int getBindedY(JGLFI info) {
		CursorCallback c = info.window.getCallback_Cursor();
		//return by == -1 ? info.sy-c.getY() : by == 0 ? info.sy/2-c.getY()+getBounds().height/2 : c.getY();
		return (int) (  c.getY()-info.sy*((by+1.0)/2) );
	}
	
	/**
	 * checks if the mouse pointer is in bounds
	 * @param info
	 * @return																																													
	 */
	final protected boolean isInBounds(JGLFI info) {
		CursorCallback c = info.window.getCallback_Cursor();
		return this.getBounds().contains((int) (  c.getX()-info.sx*((bx+1.0)/2))
									   , (int) (  c.getY()-info.sy*((by+1.0)/2))
									   );
	}
	final public JGLComponent setBounds(int x, int y, int width, int height) {
		this.bounds.x = x;
		this.bounds.y = y;
		this.bounds.width = width;
		this.bounds.height = height;
		return this;
	}
	/**
	 * Copies the given bounds to this Component
	 * @param rect
	 * @return
	 */
	final public JGLComponent copyBounds(Rectangle rect) {
		this.bounds.x = rect.x;
		this.bounds.y = rect.y;
		this.bounds.width = rect.width;
		this.bounds.height = rect.height;
		return this;
	}
	final public JGLComponent setBindings(int x_bound_right, int y_bound_up) {
		for(JGLComponent c : components)
			c.setBindings(x_bound_right, y_bound_up);
		this.bx = x_bound_right;
		this.by = y_bound_up;
		return this;
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
	final public void setCursor(int cursor) {
		this.cursor = (byte) cursor;
		for(JGLComponent c : components)
			c.setCursor(cursor);
	}
	final public void setIgnoreCursor(boolean ignoreCursor) {
		this.ignoreCursor = ignoreCursor;
		for(JGLComponent c : components)
			c.setIgnoreCursor(ignoreCursor);
	}
	final public void setHover_Element(JGLComponent hover_Element) {
		this.hover_Element = hover_Element;
		hover_Element.setVisible(false);
		components.add(hover_Element);
	}
	final public void setRightClick_Element(JGLComponent rightClick_Element) {
		this.rightClick_Element = rightClick_Element;
		rightClick_Element.setVisible(false);
		components.add(rightClick_Element);
	}
	
	public static void freeAllComponents() {
		JGLBackground.free();
		JGLBar.free();
		JGLButton.free();
		JGLPSquare.free();
		JGLSlider.free();
		JGLSlider2D.free();
		JGLSquare.free();
		JGLTable.free();
		JGLTest.free();
		JGLTextField.free();
		JGLTextInput.free();
	}
	
	//Simple
	final public boolean isIgnoreCursor() {
		return ignoreCursor;
	}
	final public boolean isHovered() {
		return isHovered;
	}
	final public boolean isVisible() {
		return isVisible;
	}
	public boolean isDragging() {
		return isDragging;
	}
	public boolean isFocused() {
		return isFocused;
	}
	final public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	final public boolean isClicking() {
		return isClicking[0];
	}
	final public boolean isRClicking() {
		return isClicking[1];
	}
	final public boolean isMClicking() {
		return isClicking[2];
	}
	final public boolean isPaintedReverse() {
		return isPaintedReverse;
	}
	final public void setPaintedReverse(boolean isPaintedReverse) {
		this.isPaintedReverse = isPaintedReverse;
	}
	final public List<JGLComponent> getComponents() {
		return components;
	}
	final public Rectangle getBounds() {
		return bounds;
	}
	final public JGLComponent setBounds(Rectangle bounds) {
		this.bounds = bounds;
		return this;
	}
	final public boolean isCarrying() {
		return carrying;
	}
	final public JGLComponent setCarrying(boolean carrying) {
		this.carrying = carrying;
		return this;
	}
}
