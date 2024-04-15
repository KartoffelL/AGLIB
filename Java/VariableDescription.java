package Kartoffel.Licht.Java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VariableDescription {

	String description();
	float max();
	float min();
	
}
