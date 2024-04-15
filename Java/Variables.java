package Kartoffel.Licht.Java;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Variables {
	
	final public static List<Field> fields = new ArrayList<>();

	public static void register(Class<?> c) {
		for (Field field : c.getDeclaredFields()) {
		    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()))
		    	if(field.getType().isPrimitive())
		    		fields.add(field);
		}
	}

}
