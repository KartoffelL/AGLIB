#version 330 core

uniform sampler2D sampler0;
uniform sampler2D sampler1;

in vec2 tex_coords;

layout (location = 0) out vec4 BloomColor;

uniform float threshhold = 1;
uniform float cla = 128;
uniform float boost = 1;
uniform float em_boost = 1.5;

void main() {
	vec4 color = texture2D(sampler0, tex_coords);
	vec4 mat = texture2D(sampler1, tex_coords);
//	float durch = (color.r+color.g+color.b)/3;
//	float max = max(max(color.r, color.g), color.b);
//	vec3 ampcol = (color.rgb*pow((max-durch), 1)-threshhold)*10;
//	BloomColor = vec4(clamp(ampcol, 0, 10), 1.0);
	float lb = (color.r+color.g+color.b)/3+mat.b*em_boost;
	if(lb > threshhold)
		BloomColor = clamp(color*boost, 0, cla);
	else
		BloomColor = vec4(0, 0, 0, 1);
}
