package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.Rectangle;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Tools.TextureUtils;

public class JGLSlider extends JGLComponent{
	
	JGLSquare square;
	JGLSquare squareS;
	
	JGLSlider slider = this;
	
	double value = 0;
	double snap = Double.POSITIVE_INFINITY;
	
	public JGLSlider() {
		this(Double.POSITIVE_INFINITY);
	}
	
	public JGLSlider(double snap) {
		super(100, 10, 500, 500);
		this.snap = snap;
		square = new JGLSquare(TextureUtils.generate3DBlock(Color.GREEN, Color.GRAY, 1, 500, 500)) {
			
			@Override
			protected void MouseClickComponent(JGLFI info, int x, int y, int button, boolean down, boolean focus) {
				if(focus && down)
					squareS.getBounds().x = x-squareS.getBounds().width/2;
				snap();
			}
			@Override
			protected void DragComponent(JGLFI info, int x, int y, int mx, int my) {
				squareS.getBounds().x = x-squareS.getBounds().width/2;
				
				if(squareS.getBounds().x < slider.getBounds().x)
					squareS.getBounds().x = slider.getBounds().x;
				if(squareS.getBounds().x > slider.getBounds().x+slider.getBounds().width-squareS.getBounds().width)
					squareS.getBounds().x = slider.getBounds().x+slider.getBounds().width-squareS.getBounds().width;
				snap();
			}
			
			private void snap() {
				if(snap != Double.POSITIVE_INFINITY)
					setValue(Math.round(getValue()*snap)/snap);
				
			}
			
		};
		squareS = new JGLSquare(Color.WHITE) {
			
		};
		square.setTexture(new Texture(TextureUtils.generate3DBlock(Color.BLUE, Color.GRAY, 5, bounds.width, bounds.height)));
		square.setBounds(getBounds());
		squareS.getBounds().width = 10;
		squareS.getBounds().height = getBounds().height;
		squareS.getBounds().y = getBounds().y;
		squareS.getBounds().x = getBounds().x;
		this.add(square);
		this.add(squareS);
	}
	
	final public double getValue() {
		return (double)(squareS.getBounds().x-getBounds().x)/(getBounds().width-squareS.getBounds().width);
	}
	
	final public void setValue(double val) {
		value = val;
		squareS.getBounds().x = (int) (val*(getBounds().width-squareS.getBounds().width)+getBounds().x);
	}
	
	final public JGLSlider setSliderThickness(int val) {
		squareS.getBounds().width = val;
		return this;
	}
	
	
	protected void onSliderMove(double value) {
		
	}
	
	double oldVal;
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		if(oldVal != getValue()) {
			oldVal = getValue();
			onSliderMove(clip(oldVal));
		}
	}
	
	final private double clip(double a) {
		return Math.max(Math.min(a, 1), 0);
	}
	
	public double getSnap() {
		return snap;
	}
	
	public void setSnap(double snap) {
		this.snap = snap;
	}
	
	@Override
	protected void ReboundComponent(Rectangle bounds) {
		if(bounds.width != this.bounds.width || bounds.height != this.bounds.height)
			square.setTexture(new Texture(TextureUtils.generate3DBlock(Color.BLUE, Color.GRAY, 5, bounds.width, bounds.height)));
		square.setBounds(getBounds());
		squareS.getBounds().height = getBounds().height;
		squareS.getBounds().y = getBounds().y;
		square.getTexture().free();
		setValue(value);
	}

	public static void free() {
		// TODO Auto-generated method stub
		
	}
	
}
