#version 330 core

uniform sampler2D sampler;

in vec2 tex_coords;
in vec2 pos;

uniform bool D3;

uniform float width = 0.0;
uniform float edge = 0.0;
uniform vec3 color;

uniform vec4 textMat;

uniform vec4 clip = vec4(-0.8, -0.8, 0.8, 0.8);


uniform vec3 sel_color = vec3(0, 0, 1);
uniform bool sel;
uniform bool selL;

vec4 getColor(vec3 col, float w, float e, vec2 offset) {
	float distance = texture(sampler, clamp(tex_coords+offset, textMat.xy, textMat.xy+textMat.zw)).r;
	float opacity = pow(smoothstep(0, w, distance), e);
	if(sel)
		return vec4(sel_color, opacity);
	else
		return vec4(col, opacity);
}

void main() {
	if(selL) {
		gl_FragColor = vec4(1-sel_color, 0.7f);
		return;
	}
	gl_FragColor = getColor(sel ? sel_color : color, width, edge, vec2(0, 0));
	
	if(gl_FragColor.a < (D3 ? 1 : 0.001))
		discard;
	if(!D3)
		if(pos.x < clip.x || pos.x > clip.z || pos.y < clip.y || pos.y > clip.w)
			discard;
	
}
