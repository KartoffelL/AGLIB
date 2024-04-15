package Kartoffel.Licht.Rendering.Shapes.CubicArrayMeshC;

public class CubeDictionary {

	public Cube[] data;
	public int TextureSize;
	public int TCS;		//TextureCountSquared
	
	public CubeDictionary(int ts, int tcs, Cube...cubes) {
		Cube[] c = new Cube[cubes.length+1];
		c[0] = new Cube(0, true);
		for(int i = 0; i < cubes.length; i++)
			c[i+1] = cubes[i];
		this.data = c;
		this.TextureSize = ts;
		this.TCS = tcs;
	}
	
	
	public Cube get(int index) {
		if(index < data.length)
			if(index >= 0)
				return data[index];
		return data[0];
	}

}
