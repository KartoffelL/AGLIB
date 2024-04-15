package Kartoffel.Licht.Rendering.Shapes;

public class SBox2D extends Shape{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5967225123685002034L;
	public SBox2D() {
		super.main(
		//####################################
		new float[] {
		-0.5f, 0.5f, 0f,	//Top Left
		 0.5f, 0.5f, 0f,	//top Right
		 0.5f, -0.5f, 0f,	//Bottom Right
		 -0.5f, -0.5f, 0f,	//Bottom Left
		},
		new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		},
		new int[] {
				0, 2, 1,
				2, 0, 3
		}
		//#####################################
		, new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1
				}
		);
	}
	/**
	 * sx & sy are relative to the rendering box (init Window size). for a object that reaches
	 * to the end of the rendering box just type in 1.
	 * 
	 * @param sx
	 * @param sy
	 */
	public SBox2D(float sx, float sy) {
		super.main(
		//####################################
		new float[] {
		-sx, sy, 0f,	//Top Left
		 sx, sy, 0f,	//top Right
		 sx, -sy, 0f,	//Bottom Right
	    -sx, -sy, 0f,	//Bottom Left
		},
		new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		},
		new int[] {
				0, 2, 1,
				2, 0, 3
		}
		//#####################################
		, new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1
				}
		);
	}
	
	public SBox2D(float sx, float sy, float px, float py) {
		super.main(
		//####################################
		new float[] {
		-sx+px, sy+py, 0f,	//Top Left
		 sx+px, sy+py, 0f,	//top Right
		 sx+px, -sy+py, 0f,	//Bottom Right
	    -sx+px, -sy+py, 0f,	//Bottom Left
		},
		new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		},
		new int[] {
				0, 2, 1,
				2, 0, 3
		}
		//#####################################
		, new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1
				}
		);
	}
	
	
	/**
	 * sx & sy are relative to the rendering box (init Window size). for a object that reaches
	 * to the end of the rendering box just type in 1.
	 * 
	 * @param px/py		Position
	 * @param width		the width
	 * @param height	the height		
	 * @param tx		texture X from 0-1;
	 * @param ty		texture Y from 0-1;
	 * @param tsx		texture Width from 0-1;
	 * @param tsy		texture Height from 0-1;
	 */
	public SBox2D(float px, float py, float width, float height, float tx, float ty, float tsx, float tsy) {
		super.main(
		//####################################
		new float[] {
		px, py, 0f,	//Top Left
		px+width, py, 0f,	//top Right
		px+width, py-height, 0f,	//Bottom Right
		px, py-height, 0f,	//Bottom Left
		},
		new float[] {
				tx,ty,		//BB
							//OB
				
				tsx,ty,		//BB
							//BO
				
				tsx,tsy,		//BO
							//BB
				
				tx,tsy			//OB
							//BB
		},
		new int[] {
				0, 2, 1,
				2, 0, 3
		}
		//#####################################
		, new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1
				}
		);
	}
	
	
	public static ShapeData XM() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				-0.5f, 0.5f, 0f,	//Top Left
				 0.5f, 0.5f, 0f,	//top Right
				 0.5f, -0.5f, 0f,	//Bottom Right
				 -0.5f, -0.5f, 0f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				0, 2, 1,
				2, 0, 3
		};
		s.nor = new float[] {
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
				-1, 0, 0,
		};
		return s;
	}
	public static ShapeData XP() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				-0.5f, 0.5f, 0f,	//Top Left
				 0.5f, 0.5f, 0f,	//top Right
				 0.5f, -0.5f, 0f,	//Bottom Right
				 -0.5f, -0.5f, 0f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				1, 2, 0,
				3, 0, 2
		};
		s.nor = new float[] {
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
				1, 0, 0,
		};
		return s;
	}
	public static ShapeData YM() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				-0.5f, 0f, 0.5f,	//Top Left
				 0.5f, 0f, 0.5f,	//top Right
				 0.5f, 0f, -0.5f,	//Bottom Right
				 -0.5f, 0f, -0.5f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				0, 2, 1,
				2, 0, 3
		};
		s.nor = new float[] {
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
				0, -1, 0,
		};
		return s;
	}
	public static ShapeData YP() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				-0.5f, 0f, 0.5f,	//Top Left
				 0.5f, 0f, 0.5f,	//top Right
				 0.5f, 0f, -0.5f,	//Bottom Right
				 -0.5f, 0f, -0.5f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				1, 2, 0,
				3, 0, 2
		};
		s.nor = new float[] {
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
				0, 1, 0,
		};
		return s;
	}
	public static ShapeData ZM() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				 0, 0.5f, -0.5f,	//Top Left
				 0f, 0.5f, 0.5f,	//top Right
				 0f, -0.5f, 0.5f,	//Bottom Right
				 0f, -0.5f, -0.5f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				0, 2, 1,
				2, 0, 3
		};
		s.nor = new float[] {
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
		};
		return s;
	}
	public static ShapeData ZP() {
		ShapeData s = new ShapeData();
		s.ver = new float[] {
				 0, 0.5f, -0.5f,	//Top Left
				 0f, 0.5f, 0.5f,	//top Right
				 0f, -0.5f, 0.5f,	//Bottom Right
				 0f, -0.5f, -0.5f,	//Bottom Left
				};
		s.tex = new float[] {
				0,0,
				1,0,
				1,1,
				0,1
		};
		s.ind = new int[] {
				0, 2, 1,
				2, 0, 3
		};
		s.nor = new float[] {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
		};
		return s;
	}

}
