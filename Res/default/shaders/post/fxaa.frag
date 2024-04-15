#version 330 core

uniform sampler2D sampler0;

in vec2 tex_coords;

uniform vec2 windowSize;

float getLum(vec3 col) {
	return (col.r+col.g+col.b)/3;
}

void main() {
	vec4 col = texture2D(sampler0, tex_coords);
	vec2 tex_offset = 1 / windowSize;
	float lum = getLum(texture2D(sampler0, tex_coords).rgb);
	
	float lAA = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(1, -1)).rgb);
	float lBA = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(1, 0)).rgb);
	float lCA = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(1, 1)).rgb);
	float lAB = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(0, 1)).rgb);
	float lCB = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(0, -1)).rgb);
	float lAC = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(-1, 1)).rgb);
	float lBC = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(-1, 0)).rgb);
	float lCC = getLum(texture2D(sampler0, tex_coords+tex_offset*vec2(-1, -1)).rgb);
	
	float lowest = min(lAA, min(lBA, min(lCA, min(lAB, min(lCB, min(lAC, min(lBC, lCC)))))));
	float highest = max(lAA, max(lBA, max(lCA, max(lAB, max(lCB, max(lAC, max(lBC, lCC)))))));
	float contrast = highest-lowest;
	float blend = (lAA+lBA+lCA+lAB+lCB+lAC+lBC+lCC)/8;
	if(contrast > .05) {
		float a = clamp(abs(lum-blend)/contrast, 0, 1);
		a = smoothstep(0, 1, a);
		a = a*a;
		
		float hor = abs(2*(lAB + lAC - 2*lum))+abs(lAA + lCA - 2*lBA)+abs(lAC + lCC - 2*lBC);
		float ver = abs(2*(lBA + lBC - 2*lum))+abs(lAC + lCA - 2*lAB)+abs(lAA + lCC - 2*lCB);
		
		vec2 dir = vec2(0, 0);
		if(hor >= ver) { //Vertical
			float pc = abs((lCA+lAB+lAC)/3-lum);
			float nc = abs((lAA+lCB+lCC)/3-lum);
			if(pc < nc)
				dir = vec2(0, -1);
			else
				dir = vec2(0, 1);
		}
		else {
			float pc = abs((lAA+lBA+lCA)/3-lum);
			float nc = abs((lAC+lBC+lCC)/3-lum);
			if(pc < nc)
				dir = vec2(-1, 0);
			else
				dir = vec2(1, 0);
		}
		
		gl_FragColor = vec4(texture2D(sampler0, tex_coords+tex_offset*dir).rgb*a+col.rgb*(1-a), col.a);
			
	}
	else
		gl_FragColor = vec4(col);
}
