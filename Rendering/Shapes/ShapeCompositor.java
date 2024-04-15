package Kartoffel.Licht.Rendering.Shapes;

import org.joml.Matrix4f;

import Kartoffel.Licht.Engine.CubeArray3D;
import Kartoffel.Licht.Engine.WaveFunctionCollapse;
import Kartoffel.Licht.Engine.WaveFunctionCollapse.cube;
import Kartoffel.Licht.Engine.WaveFunctionCollapse.state;
import Kartoffel.Licht.Rendering.CompositeModel;
import Kartoffel.Licht.Rendering.InstancedModel;

public class ShapeCompositor extends Shape{
	private static final long serialVersionUID = 1L;

	public static CompositeModel composite(CubeArray3D data, ShapeData...shapes) {
		CompositeModel cm = new CompositeModel();

		Matrix4f m = new Matrix4f();
		for(int j = 0; j < shapes.length; j++) {
			InstancedModel im = new InstancedModel(shapes[j], data.count((short) (j+1)), InstancedModel.ADT_TRANSMATRIX);
			int index = 0;
			for(int i = 0; i < data.getWidth(); i++) {
				for(int l = 0; l < data.getHeight(); l++) {
					for(int k = 0; k < data.getDepth(); k++) {
						if(data.getData()[i][l][k] != j+1)
							continue;
						m.identity();
						m.translate(i, l, k);
						im.setInstanceAttribMatrix(index, 0, m);
						index++;
					}
				}
			}
			im.updateAllData();
			cm.getModels().add(im);
		}
		return cm;
	}
	
	public static CompositeModel composite(cube[][][] data, ShapeData...shapes) {
		CompositeModel cm = new CompositeModel();
		cm.setFaceCulling(false);
		Matrix4f m = new Matrix4f();
		for(int j = 0; j < shapes.length; j++) {
			int count = 0;
			for(int i = 0; i < data.length; i++) {
				for(int l = 0; l < data[0].length; l++) {
					for(int k = 0; k < data[0][0].length; k++) {
						if(data[i][l][k] != null)
							if(data[i][l][k].type.id == j)
								count++;
					}
				}
			}
			InstancedModel im = new InstancedModel(shapes[j], count, InstancedModel.ADT_TRANSMATRIX);
			int index = 0;
			for(int i = 0; i < data.length; i++) {
				for(int l = 0; l < data[0].length; l++) {
					for(int k = 0; k < data[0][0].length; k++) {
						if(data[i][l][k] == null)
							continue;
						if(data[i][l][k].type.id != j)
							continue;
						m.identity();
						state s = WaveFunctionCollapse.getState(data[i][l][k].type.state);
						m.translate(i, l, k);
						m.rotateX((float)(s.rotX/2.0*Math.PI)).rotateY((float)(s.rotY/2.0*Math.PI)).rotateZ((float)(s.rotZ/2.0*Math.PI));
						im.setInstanceAttribMatrix(index, 0, m);
						index++;
					}
				}
			}
			im.updateAllData();
			cm.getModels().add(im);
		}
		return cm;
	}
	
	public ShapeCompositor(ShapeData...shapes) {
		int amountPoints = 0;
		int amountInd = 0;
		for(ShapeData s : shapes) {
			amountPoints += s.ver.length/3;
			amountInd += s.ind.length;
		}
		this.ind = new int[amountInd];
		this.ver = new float[amountPoints*3];
		this.nor = new float[amountPoints*3];
		this.tex = new float[amountPoints*2];
		this.mat = new int[amountPoints];
		int offset = 0;
		int ioffset = 0;
		for(int i = 0; i < shapes.length; i++) {
			ShapeData s = shapes[i];
			System.arraycopy(s.ver, 0, this.ver, offset*3, s.ver.length);
			System.arraycopy(s.nor, 0, this.nor, offset*3, s.nor.length);
			System.arraycopy(s.tex, 0, this.tex, offset*2, s.tex.length);
			for(int j = 0; j < s.ver.length/3; j++)
				this.mat[offset+j] = i;
			for(int j = 0; j < s.ind.length; j++)
				this.ind[ioffset+j] = s.ind[j]+ioffset;
			offset += s.ver.length/3;
			ioffset += s.ind.length;
		}
	}

}
