package Kartoffel.Licht.Rendering.Shapes;

public class SBox3D extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7983879309929622303L;

	/**
	 * A 3 Dimensional Box.
	 * The Width of the box is double the size:
	 * 
	 * width = size*2
	 * 
	 * @param sx
	 * The X-Size of the box
	 * @param sy
	 * The Y-Size of the box
	 *  @param sz
	 *  The Z-Size of the box
	 */
	public SBox3D(float sx, float sy, float sz) {
		m(sx, sy, sz);
	}
	
	/**
	 * A 3 Dimensional Box.
	 * The Width of the box is double the size:
	 * 
	 * width = size*2
	 * 
	 * @param sx
	 * The X-Size of the box
	 * @param sy
	 * The Y-Size of the box
	 *  @param sz
	 *  The Z-Size of the box
	 *  @param inverse 
	 *  If the box needs to be inverted, for Skyboxes, etc..
	 */
	public SBox3D(float sx, float sy, float sz, boolean inverse) {
		if(!inverse)
			m(sx, sy, sz);
		else
			super.main(
					//####################################
					new float[] {
							-sx,sy,-sz,		//-sz
							-sx,-sy,-sz,	
							sx,-sy,-sz,	
							sx,sy,-sz,		
							
							-sx,sy,sz,		//sz
							-sx,-sy,sz,	
							sx,-sy,sz,	
							sx,sy,sz,
							
							sx,sy,-sz,		//sx
							sx,-sy,-sz,	
							sx,-sy,sz,	
							sx,sy,sz,
							
							-sx,sy,-sz,		//-sx
							-sx,-sy,-sz,	
							-sx,-sy,sz,	
							-sx,sy,sz,
							
							-sx,sy,sz,		//sy
							-sx,sy,-sz,
							sx,sy,-sz,
							sx,sy,sz,
							
							-sx,-sy,sz,		//-sy
							-sx,-sy,-sz,
							sx,-sy,-sz,
							sx,-sy,sz},
					new float[] {
							0,0,
							0,1,
							1,1,
							1,0,			
							0,0,
							0,1,
							1,1,
							1,0,			
							0,0,
							0,1,
							1,1,
							1,0,
							0,0,
							0,1,
							1,1,
							1,0,
							0,0,
							0,1,
							1,1,
							1,0,
							0,0,
							0,1,
							1,1,
							1,0
					},
					new int[] {
							3,0,1,	//-z
							2,3,1,	
							
							4,7,5,	//z ok
							7,6,5,
							
							11,8,9,	//x
							10,11,9,
							
							12,15,13,	//-x ok
							15,14,13,
							
							19,16,17,	//y
							18,19,17,
							
							20,23,21,	//-y ok
							23,22,21
							
					},
					new float[] {
							0, 0, -1,
							0, 0, -1,
							0, 0, -1,
							0, 0, -1,
							
							0, 0, 1,
							0, 0, 1,
							0, 0, 1,
							0, 0, 1,
							
							1, 0, 0,
							1, 0, 0,
							1, 0, 0,
							1, 0, 0,
							
							-1, 0, 0,
							-1, 0, 0,
							-1, 0, 0,
							-1, 0, 0,
							
							0, 1, 0,
							0, 1, 0,
							0, 1, 0,
							0, 1, 0,
							
							0, -1, 0,
							0, -1, 0,
							0, -1, 0,
							0, -1, 0,
							
							
							},
					null,
					null,
					null
					//#####################################
					);
	}
	
	private void m(float sx, float sy, float sz) {
		super.main(
				//####################################
				new float[] {
						-sx,sy,-sz,		//-sz
						-sx,-sy,-sz,	
						sx,-sy,-sz,	
						sx,sy,-sz,		
						
						-sx,sy,sz,		//sz
						-sx,-sy,sz,	
						sx,-sy,sz,	
						sx,sy,sz,
						
						sx,sy,-sz,		//sx
						sx,-sy,-sz,	
						sx,-sy,sz,	
						sx,sy,sz,
						
						-sx,sy,-sz,		//-sx
						-sx,-sy,-sz,	
						-sx,-sy,sz,	
						-sx,sy,sz,
						
						-sx,sy,sz,		//sy
						-sx,sy,-sz,
						sx,sy,-sz,
						sx,sy,sz,
						
						-sx,-sy,sz,		//-sy
						-sx,-sy,-sz,
						sx,-sy,-sz,
						sx,-sy,sz},
				new float[] {
						0,0,
						0,1,
						1,1,
						1,0,			
						0,0,
						0,1,
						1,1,
						1,0,			
						0,0,
						0,1,
						1,1,
						1,0,
						0,0,
						0,1,
						1,1,
						1,0,
						0,0,
						0,1,
						1,1,
						1,0,
						0,0,
						0,1,
						1,1,
						1,0
				},
				new int[] {
						3,1,0,	//-z
						2,1,3,	
						
						4,5,7,	//z ok
						7,5,6,
						
						11,9,8,	//x
						10,9,11,
						
						12,13,15,	//-x ok
						15,13,14,
						
						19,17,16,	//y
						18,17,19,
						
						20,21,23,	//-y ok
						23,21,22
						
				},
				new float[] {
						0, 0, -1,
						0, 0, -1,
						0, 0, -1,
						0, 0, -1,
						
						0, 0, 1,
						0, 0, 1,
						0, 0, 1,
						0, 0, 1,
						
						1, 0, 0,
						1, 0, 0,
						1, 0, 0,
						1, 0, 0,
						
						-1, 0, 0,
						-1, 0, 0,
						-1, 0, 0,
						-1, 0, 0,
						
						0, 1, 0,
						0, 1, 0,
						0, 1, 0,
						0, 1, 0,
						
						0, -1, 0,
						0, -1, 0,
						0, -1, 0,
						0, -1, 0,
						
						
						}
				//#####################################
				);
	}

}
