package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.Rectangle;
import Kartoffel.Licht.Tools.TextureUtils;

public class JGLSlider2D extends JGLComponent{
	
	JGLSquare square;
	JGLSquare squareS;
	
	JGLSlider2D slider = this;
	
	private boolean circular = false;
	
	
	double oldValX;
	double oldValY;
	double distance;
	double max_distance = 50;
	double xy, yx;
	double rot;
	
	public JGLSlider2D() {
		super(100, 10, 500, 500);
		square = new JGLSquare(TextureUtils.generateColorWheel(0, 512, false, 0)) {
			
			@Override
			protected void MouseClickComponent(JGLFI info, int x, int y, int button, boolean down, boolean focus) {
				if(focus)
					m(x, y);
			}
			
			@Override
			protected void DragComponent(JGLFI info, int x, int y, int mx, int my) {
				m(x, y);
			}
			
			protected void m(int x, int y) {
				if(isCircular()) {
					//Calculate the maximum distance
					max_distance = Math.min(slider.getBounds().getWidth(), slider.getBounds().getHeight());
					//Calculate the aspect ratio
					//Calculate the current distance
					distance = Math.sqrt(Math.pow((slider.getBounds().getCenterX()-x), 2)+Math.pow((slider.getBounds().getCenterY()-y), 2));
					//clip distance to maximum
					distance = Math.min(distance, max_distance);
					//Calculate rotation
					rot = Math.atan2(
									slider.getBounds().getCenterX()-x,
									slider.getBounds().getCenterY()-y
									);
					//Apply position using distance & rotation
					squareS.getBounds().x = (int) (  (-Math.sin(rot)*distance)  +slider.getBounds().getCenterX())-squareS.getBounds().width/2;
					squareS.getBounds().y = (int) (  (-Math.cos(rot)*distance)  +slider.getBounds().getCenterY())-squareS.getBounds().height/2;
				} else {
					squareS.getBounds().x = org.joml.Math.clamp(square.getBounds().x, square.getBounds().x+square.getBounds().width, x-squareS.getBounds().width/2);
					squareS.getBounds().y = org.joml.Math.clamp(square.getBounds().y, square.getBounds().y+square.getBounds().height, y-squareS.getBounds().height/2);
				}
			}
			
		};
		squareS = new JGLSquare(TextureUtils.generateCircle(Color.BLACK, 16, 0)) {
			
			
		};
		this.add(square);
		this.add(squareS);
		
		this.setCursor(2);
	}
	
	final public double getValueX() {
		return (double)(squareS.getBounds().x-getBounds().x)/(getBounds().width-squareS.getBounds().width);
	}
	final public double getValueY() {
		return (double)(squareS.getBounds().y-getBounds().y)/(getBounds().height-squareS.getBounds().height);
	}
	
	final public double getRotation() {
		return Math.atan2(
				slider.getBounds().getCenterX()-squareS.getBounds().getCenterX(),
				slider.getBounds().getCenterY()-squareS.getBounds().getCenterY()
				);
	}
	final public double getDistance() {
		return Math.sqrt(Math.pow(slider.getBounds().getCenterX()-squareS.getBounds().getCenterX(), 2)+Math.pow((slider.getBounds().getCenterY()-squareS.getBounds().getCenterY()), 2));
	}
	final public JGLSlider2D setSliderThickness(int val) {
		squareS.getBounds().width = val;
		squareS.getBounds().height = val;
		return this;
	}
	
	
	protected void onSliderMove(double x, double y, double rotation, double distance) {
		
	}
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		if(oldValX != getValueX() || oldValY != getValueY()) {
			oldValX = getValueX();
			oldValY = getValueY();
			onSliderMove(clip(oldValX), clip(oldValY), clip(0.5-getRotation()/(Math.PI*2)), clip(Math.min(getDistance()/max_distance, 1)));
		}
	}
	
	final private double clip(double a) {
		return Math.max(Math.min(a, 1), 0);
	}
	
	@Override
	protected void ReboundComponent(Rectangle bounds) {
		square.setBounds(getBounds());
		squareS.getBounds().x = (int) getBounds().getCenterX();
		squareS.getBounds().y = (int) getBounds().getCenterY();
		squareS.getBounds().width = 10;
		squareS.getBounds().height = 10;
	}
	
	final public JGLSlider2D setCircular(boolean circular) {
		this.circular = circular;
		return this;
	}
	
	final public boolean isCircular() {
		return circular;
	}

	public static void free() {
		// TODO Auto-generated method stub
		
	}
}
