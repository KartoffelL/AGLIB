package Kartoffel.Licht.Engine;

import org.joml.Vector3f;

import Kartoffel.Licht.Geo.translate;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Tools.Tools;

public class THeightMap implements translate{

	private BufferedImage image;
	private float ox = 0, oy = 0, sx = 1, sy = 1, sh;
	public boolean stacked = false;
	
	public THeightMap(BufferedImage image) {
		this.image = image;
	}

	public THeightMap(BufferedImage image, float ox, float oy, float sx, float sy, float sh, boolean stacked) {
		super();
		this.image = image;
		this.ox = ox;
		this.oy = oy;
		this.sx = sx;
		this.sy = sy;
		this.sh = sh;
		this.stacked = stacked;
	}

	@Override
	public Vector3f m(float x, float y, float z, int ver) {
		return new Vector3f(x, get(x/sx+ox, z/sy+oy)*sh, z);
	}
	
	private float get(float x, float z) {
		float[] f = Tools.getPresiceRGB_Billing(image, x*image.getWidth(), z*image.getHeight(), null);
		return stacked ? f[0]+f[1]+f[2] : f[0];
	}

}
