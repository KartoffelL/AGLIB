package Kartoffel.Licht.Engine;

import java.util.Random;

import org.joml.SimplexNoise;
import org.joml.Vector3f;

import Kartoffel.Licht.Geo.translate;

public class TTestMap implements translate{
	
	Random random;
	double xoff, yoff;
	public TTestMap(long seed) {
		 random = new Random(seed);
		 this.xoff = random.nextInt(3000);
		 this.yoff = random.nextInt(3000);
	}
	
	public TTestMap() {
		random = new Random();
		this.xoff = random.nextInt(3000);
		this.yoff = random.nextInt(3000);
	}
	
	
	@Override
	public Vector3f m(float x, float y, float z, int ver) {
		// TODO Auto-generated method stub
		return new Vector3f(x, get((float)(x+xoff), (float)(z+yoff)), z);
	}
	
	public static float get(float x, float z) {
//		double base = SimplexNoise.noise(x/512, z/512)*32; //32
//		double biom = SimplexNoise.noise(x/256, z/256)*.5f+.5f;
//		
//		double plains = SimplexNoise.noise(x/16, z/16)*.5f+.5f;
//		
//		double mountains = SimplexNoise.noise(x/64, z/64)*.5f+.5f;
//		
//		double plat = hardStep(SimplexNoise.noise(x/128, z/128), 0.6)*8;
//		
//		base += plains*biom; //+1 = -31-33
//		base += mountains*(1-biom)*8; //+8 = -23-41
//		base += mountains*Math.pow(1-biom, 8)*base*8; //+8 = -15-49
//		base += plat*org.joml.Math.clamp(0, 1, base/16.0); //+-8 = -23-57
//		return (float) (base-17)/40;
		
		float base = SimplexNoise.noise(x/64, z/64);
		base += SimplexNoise.noise(x/16, z/16, base);
		return base;
	}
	
	public static double hardStep(double val, double hard) {
		return Math.pow(Math.abs(val), hard)*Math.signum(val);
	}
	

}
