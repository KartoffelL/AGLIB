package Kartoffel.Licht.Geo;

import java.util.Random;

import org.joml.Vector3d;

public interface DefinableShape {
	
	public double intersection(Ray r, boolean canBeInside);
	
	public Vector3d randomPoint(Random random, Vector3d targed);
	
	public AABB getBoundingBox();
	
	public DefinableShape clone();

}
