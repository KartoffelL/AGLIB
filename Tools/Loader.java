package Kartoffel.Licht.Tools;

import Kartoffel.Licht.Rendering.Shapes.Shape;
import Kartoffel.Licht.Rendering.Texture.Material.Material;

public class Loader extends Shape{
	private static final long serialVersionUID = 4002685742143245786L;
	Shape[] shapes;
	Material[] materials;
	
	public Material[] getMaterials() {
		return materials;
	}
	public Shape[] getShapes() {
		return shapes;
	}

}
