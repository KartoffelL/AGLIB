package Kartoffel.Licht.Rendering.Shapes.CubicArrayMeshC;

public class CubicArrayMeshData {

	public int SizeX;
	public int SizeY;
	public int SizeZ;
	public int offX, offY, offZ;
	//public int[][][] data;
	public CubePlacingAlg c;
	
	public CubicArrayMeshData(int SizeX, int SizeY, int SizeZ, CubePlacingAlg c) {
		this.SizeX = SizeX;
		this.SizeY = SizeY;
		this.SizeZ = SizeZ;
		//data = new int[SizeX][SizeY][SizeZ];
		//this.build(c);
		this.c = c;
	}
	
	public CubicArrayMeshData(int SizeX, int SizeY, int SizeZ, CubePlacingAlg c, int ox, int oy, int oz) {
		this.SizeX = SizeX;
		this.SizeY = SizeY;
		this.SizeZ = SizeZ;
		this.offX = ox;
		this.offY = oy;
		this.offZ = oz;
		//data = new int[SizeX][SizeY][SizeZ];
		//this.build(c);
		this.c = c;
	}
	
	public CubicArrayMeshData(int[][][] data) {
		this.SizeX = data.length;
		this.SizeY = data[0].length;
		this.SizeZ = data[0][0].length;
		this.offX = 0;
		this.offY = 0;
		this.offZ = 0;
		this.c = new CubePlacingAlg() {
			
			@Override
			public int get(int x, int y, int z) {
				if(data.length > x && x >= 0)
					if(data[x].length > y && y >= 0)
						if(data[x][y].length > z && z >= 0)
							return data[x][y][z];
				return 0;
			}
		};
	}
	
	
	
	public int getCube(int x, int y, int z) {
		return c.get(x+offX, y+offY, z+offZ);
	}

}
