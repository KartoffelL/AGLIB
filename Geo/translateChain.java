package Kartoffel.Licht.Geo;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class translateChain implements translate{

	public List<translate> translates = new ArrayList<>();

	public translateChain(translate...translates) {
		for(translate t : translates)
			this.translates.add(t);
	}
	
	public void append(translate...translates) {
		for(translate t : translates)
			this.translates.add(t);
	}
	
	public translate append(translate t) {
		this.translates.add(t);
		return t;
	}
	
	@Override
	public Vector3f m(float x, float y, float z, int ver) {
		Vector3f trans = new Vector3f(x, y, z);
		for(translate t : translates)
			trans.set(t.m(trans.x, trans.y, trans.z, ver));
		return trans;
	}

}
