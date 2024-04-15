package Kartoffel.Licht.Geo;

import org.joml.Vector3f;

public interface translate {
	
	Vector3f m(float x, float y, float z, int ver);

	default Vector3f m(Vector3f m, int ver) {
		return m(m.x, m.y, m.z, ver);
	}
	
}