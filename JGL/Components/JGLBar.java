package Kartoffel.Licht.JGL.Components;

import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.Rectangle;

public class JGLBar extends JGLComponent{

	JGLSquare square;
	
	private int spacing = 5;
	private boolean movable = true;
	
	private JGLTable table;
	
	private Rectangle min_bounds = new Rectangle(), max_bounds = new Rectangle();
	
	public JGLBar() {
		super(0, 0, 200, 30);
		this.setBindings(-1, -1);
		square = new JGLSquare(Color.GRAY);
		add(square);
		isPaintedReverse = false;
	}
	
	public JGLBar(Color col) {
		super(0, 0, 200, 30);
		this.setBindings(-1, -1);
		square = new JGLSquare(col);
		this.cursor = 0;
		add(square);
	}
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		this.getBounds().width = info.sx;
		square.setBounds(this.getBounds());
	}
	
	@Override
	protected void ReboundComponent(Rectangle bounds) {
		square.setBounds(getBounds());
	}
	
	final public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
	
	final public JGLBar setTable(JGLTable table) {
		this.components.remove(this.table);
		this.components.add(table);
		this.table = table;
		return this;
	}
	
	@Override
	protected void DragComponent(JGLFI info, int x, int y, int mx, int my) {
		if(movable && table != null) {
			
			this.bounds.x -= mx;
			this.bounds.y -= my;
			for(JGLComponent c : components) {
				if(c != table && c != square) {
					c.getBounds().x -= mx;
					c.getBounds().y -= my;
				}
			}
			table.transform(-mx, -my);
			this.pack();
			
		}
	}
	
	final public JGLTable getTable() {
		return table;
	}
	int off = 0;
	List<JGLButton> buttons = new ArrayList<>();
	final public JGLBar addButtons(JGLButton...b) {
		for(int i = 0; i < b.length; i++) {
			JGLButton c = b[i];
			c.getBounds().x = off+bounds.x;
			c.getBounds().y = bounds.y;
			off += c.getBounds().width+spacing;
			c.getBounds().height = getBounds().height;
			c.pack();
			this.components.add(c);
			this.buttons.add(c);
		}
		return this;
	}
	
	public List<JGLButton> getButtons() {
		return buttons;
	}
	
	final public void clearButtons() {
		off = 0;
		this.components.removeAll(buttons);
	}
	
	
	final public void setMax_bounds(Rectangle max_bounds) {
		this.max_bounds = max_bounds;
	}
	
	final public void setMin_bounds(Rectangle min_bounds) {
		this.min_bounds = min_bounds;
	}
	
	final public Rectangle getMax_bounds() {
		return max_bounds;
	}

	final public Rectangle getMin_bounds() {
		return min_bounds;
	}
	
	public JGLBar setMovable(boolean movable) {
		this.movable = movable;
		return this;
	}

	public static void free() {
		
	}
	
}
