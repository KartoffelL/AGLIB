package Kartoffel.Licht.Engine;

import org.joml.Math;
import org.joml.SimplexNoise;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Tools.Tools;

public class TexturePainter {
	
	static public abstract class instance {
		
		public abstract void accept(float[] components, float x, float y, float width, float height, float px, float py);
		
	}
	
	static public abstract class Modifier {
		public Modifier(float...settings) {
			System.arraycopy(settings, 0, this.settings, 0, Math.min(settings.length, this.settings.length));
		}
		protected final float[] settings = new float[16];
		public Modifier settings(float...settings) {
			System.arraycopy(settings, 0, this.settings, 0, Math.min(settings.length, this.settings.length));
			return this;
		}
		public float get(int i) {
			return settings[i];
		}
		public abstract void position(float[] components, float[] position, float px, float py);
	}
	static public class ModifierImpl extends Modifier{

		public ModifierImpl(float...settings) {
			System.arraycopy(settings, 0, this.settings, 0, Math.min(settings.length, this.settings.length));
		}
		
		
		@Override
		public void position(float[] components, float[] position, float px, float py) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static final Modifier IDENTITY = new Modifier(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1) {
		@Override
		public Modifier settings(float...settings) {
			throw new RuntimeException("Cant modify identity Modifier!");
		};
		@Override
		public void position(float[] components, float[] position, float px, float py) {
			
		}
	};
	
	public static final int TEXTURE_TYPE_GRAIN = 0,
							TEXTURE_TYPE_SIMPLEX = 1,
							TEXTURE_TYPE_CARPET = 2,
							TEXTURE_TYPE_WORLEY_DISTANCE = 3,
							TEXTURE_TYPE_WORLEY_ID = 4,
							TEXTURE_TYPE_5 = 5,
							TEXTURE_TYPE_6 = 6,
							TEXTURE_TYPE_7 = 7,
							TEXTURE_TYPE_UNDEFINED = -1;
	
	private BufferedImage bi;

	public TexturePainter(BufferedImage bi) {
		this.bi = bi;
	}
	
	public void demo() {
		Color c = Color.CORAL.darker();
		fill(Color.BLACK)
		.applyTexture(10f/1024, 10f/1024, 100, 215, 1, -1, c.darker().darker(), new ModifierImpl(10, 10), TEXTURE_TYPE_WORLEY_ID)
		.applyTexture(1, 1, 1, 1, .4f, -1, c.darker().darker(), IDENTITY, TEXTURE_TYPE_GRAIN);
	}
	
	public TexturePainter fill(Color color) {
		bi.getGraphics().fill(0, 0, bi.getWidth(), bi.getHeight(), color);
		return this;
	}
	
	public TexturePainter applyHeight(float sx, float sy, float ox, float oy, float density, float sharpness, float intensity, int channel, Color color, Modifier modifier) {
		for(int i = 0; i < bi.getWidth(); i++) {
			for(int l = 0; l < bi.getHeight(); l++) {
				float[] b = bi.getComponents(i, l);
				float f = (float) java.lang.Math.pow(Math.cos(SimplexNoise.noise(i*sx+ox, l*sy+oy)*density), sharpness);
				for(int c = Math.max(0, channel); c < (channel == -1 ? Math.min(b.length, 3) : channel+1); c++) {
					b[c] = Math.lerp(color.getComponentFloat(c), b[c], f);
				}
				bi.setComponents(i, l, b);
			}
		}
		return this;
	}
	
	public TexturePainter apply(instance instance, float sx, float sy, float ox, float oy, Modifier modifier) {
		for(int i = 0; i < bi.getWidth(); i++) {
			for(int l = 0; l < bi.getHeight(); l++) {
				float[] b = bi.getComponents(i, l);
				instance.accept(b, i*sx+ox, l*sy+oy, bi.getWidth()*sx, bi.getHeight()*sy, (float)i/bi.getWidth(), (float)l/bi.getHeight());
				bi.setComponents(i, l, b);
			}
		}
		return this;
	}
	
	private float g(float x, float y, int type, float px, float py, Modifier mod) {
		switch (type) {
		case TEXTURE_TYPE_GRAIN:
			return Tools.RANDOM.nextFloat();
		case TEXTURE_TYPE_SIMPLEX:
			return (float) (SimplexNoise.noise(x, y)*.5+.5);
		case TEXTURE_TYPE_CARPET: {
			float sin1 = (float) Math.sin(py*Math.PI*20*mod.get(0));
			float cos1 = (float) Math.cosFromSin(sin1, py*Math.PI*20*mod.get(1));
			float sin2 = (float) Math.sin(px*Math.PI*20*mod.get(2));
			float cos2 = (float) Math.cosFromSin(sin2, px*Math.PI*20*mod.get(3));
			return SimplexNoise.noise((float)(x*(0.5-Math.abs(px*2-1)*.5)*cos1+sin1), (float)(y*(0.5-Math.abs(py*2-1)*.5)*cos2+sin2));
		}
		case TEXTURE_TYPE_WORLEY_DISTANCE:
			return worley(x, y, mod.get(0), mod.get(1))[0];
		case TEXTURE_TYPE_WORLEY_ID:
			return worley(x, y, mod.get(0), mod.get(1))[1];
		default:
			return 1;
		}
	}
	public static float[] worley(float x, float y, float rx, float ry) {
		int hm = hash(Integer.hashCode(mod((int)Math.floor(x), rx)),Integer.hashCode(mod((int)Math.floor(y), ry)));
		int ha = hash(Integer.hashCode(mod((int)Math.floor(x)-1, rx)),Integer.hashCode(mod((int)Math.floor(y), ry)));
		int hb = hash(Integer.hashCode(mod((int)Math.floor(x)+1, rx)),Integer.hashCode(mod((int)Math.floor(y), ry)));
		int hc = hash(Integer.hashCode(mod((int)Math.floor(x), rx)),Integer.hashCode(mod((int)Math.floor(y)-1, ry)));
		int hd = hash(Integer.hashCode(mod((int)Math.floor(x), rx)),Integer.hashCode(mod((int)Math.floor(y)+1, ry)));
		int he = hash(Integer.hashCode(mod((int)Math.floor(x)-1, rx)),Integer.hashCode(mod((int)Math.floor(y)-1, ry)));
		int hf = hash(Integer.hashCode(mod((int)Math.floor(x)+1, rx)),Integer.hashCode(mod((int)Math.floor(y)+1, ry)));
		int hg = hash(Integer.hashCode(mod((int)Math.floor(x)+1, rx)),Integer.hashCode(mod((int)Math.floor(y)-1, ry)));
		int hh = hash(Integer.hashCode(mod((int)Math.floor(x)-1, rx)),Integer.hashCode(mod((int)Math.floor(y)+1, ry)));
		float[] m = new float[] {scamble(hm), scamble(hm*hm*hm)};
		float[] a = new float[] {scamble(ha)-1, scamble(ha*ha*ha)};
		float[] b = new float[] {scamble(hb)+1, scamble(hb*hb*hb)};
		float[] c = new float[] {scamble(hc), scamble(hc*hc*hc)-1};
		float[] d = new float[] {scamble(hd), scamble(hd*hd*hd)+1};
		float[] e = new float[] {scamble(he)-1, scamble(he*he*he)-1};
		float[] f = new float[] {scamble(hf)+1, scamble(hf*hf*hf)+1};
		float[] g = new float[] {scamble(hg)+1, scamble(hg*hg*hg)-1};
		float[] h = new float[] {scamble(hh)-1, scamble(hh*hh*hh)+1};
		float nx = mod(x);
		float ny = mod(y);
		float res = Float.MAX_VALUE;
		float pres = res;
		int ind = 0;
		res = Math.min(res, ((nx-m[0])*(nx-m[0])+(ny-m[1])*(ny-m[1])));
		ind = pres != res ? hm : ind;
		pres = res;
		res = Math.min(res, ((nx-a[0])*(nx-a[0])+(ny-a[1])*(ny-a[1])));
		ind = pres != res ? ha : ind;
		pres = res;
		res = Math.min(res, ((nx-b[0])*(nx-b[0])+(ny-b[1])*(ny-b[1])));
		ind = pres != res ? hb : ind;
		pres = res;
		res = Math.min(res, ((nx-c[0])*(nx-c[0])+(ny-c[1])*(ny-c[1])));
		ind = pres != res ? hc : ind;
		pres = res;
		res = Math.min(res, ((nx-d[0])*(nx-d[0])+(ny-d[1])*(ny-d[1])));
		ind = pres != res ? hd : ind;
		pres = res;
		res = Math.min(res, ((nx-e[0])*(nx-e[0])+(ny-e[1])*(ny-e[1])));
		ind = pres != res ? he : ind;
		pres = res;
		res = Math.min(res, ((nx-f[0])*(nx-f[0])+(ny-f[1])*(ny-f[1])));
		ind = pres != res ? hf : ind;
		pres = res;
		res = Math.min(res, ((nx-g[0])*(nx-g[0])+(ny-g[1])*(ny-g[1])));
		ind = pres != res ? hg : ind;
		pres = res;
		res = Math.min(res, ((nx-h[0])*(nx-h[0])+(ny-h[1])*(ny-h[1])));
		ind = pres != res ? hh : ind;
		pres = res;
		final float magic = 0.4850712500726659470f;
		return new float[] {res*magic, scamble(ind)};
	}
	/**
	 * Good enough.
	 * @param a
	 * @param b
	 * @return
	 */
	private static int hash(int a, int b) {
		long res = a*66+b*4562+a*b;
		res |= a*6110;
		res &= res^a*b;
		return Long.hashCode(res);
	}
	private static float scamble(int i) {
		double x = i*Math.PI;
		return (float) (x-Math.floor(x));
	}
	private static float mod(float i) {
		return i-Math.floor(i);
	}
	private static int mod(int a, float b) {
		float d = a;
		d = mod(d/b)*b;
		return Math.round(d);
	}
	
	public TexturePainter applyTexture(float sx, float sy, float ox, float oy, float intensity, int channel, Color color, Modifier modifier, int TYPE) {
		float p[] = new float[2];
		for(int i = 0; i < bi.getWidth(); i++) {
			for(int l = 0; l < bi.getHeight(); l++) {
				float[] b = bi.getComponents(i, l);
				p[0] = i*sx+ox;
				p[1] = l*sy+oy;
				modifier.position(b, p, (float)i/bi.getWidth(), (float)l/bi.getHeight());
				float f = (g(p[0], p[1], TYPE, (float)i/bi.getWidth(), (float)l/bi.getHeight(), modifier))*intensity+1-intensity;
				for(int c = Math.max(0, channel); c < (channel == -1 ? Math.min(b.length, 3) : channel+1); c++) {
					b[c] = Math.lerp(color.getComponentFloat(c), b[c], f);
				}
				bi.setComponents(i, l, b);
			}
		}
		return this;
	}
	
	public BufferedImage getBufferedImage() {
		return bi;
	}
	
	public void setBufferedImage(BufferedImage bi) {
		this.bi = bi;
	}

}
