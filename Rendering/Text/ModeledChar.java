package Kartoffel.Licht.Rendering.Text;

import org.joml.Vector2f;
import org.joml.Vector4f;

import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Texture.Texture;

public class ModeledChar {
	
	private Model model; //Not owned by the class
	private Texture texture;
	
	private SChar schar;
	private Vector4f texture_Bounds;
	private Vector2f model_Bounds;
	private boolean visible = true;
	private int FLAGS = 0;
	private float height;

	public ModeledChar(Model m, SChar s, Vector4f texture_Bounds, Vector2f modelBounds, int Flags, Texture texture, float height) {
		this.model = m;
		this.schar = s;
		this.texture_Bounds = texture_Bounds;
		this.model_Bounds = modelBounds;
		this.FLAGS = Flags;
		this.texture = texture;
		this.height = height;
	}
	public Model getModel() {
		return model;
	}
	public SChar getSchar() {
		return schar;
	}
	public Vector4f getTexture_Bounds() {
		return texture_Bounds;
	}
	public Vector2f getModel_Bounds() {
		return model_Bounds;
	}
	public boolean isVisible() {
		return visible;
	}
	public int getFLAGS() {
		return FLAGS;
	}
	public Texture getTexture() {
		return texture;
	}
	public void setFLAGS(int fLAGS) {
		FLAGS = fLAGS;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public float getHeight() {
		return height;
	}
}
