#version 330 core

uniform sampler2D sampler0;
uniform sampler2D sampler1;

in vec2 tex_coords;

uniform vec3 cp;

uniform float a = 8;
uniform float c = 32;
uniform vec4 color;


void main() {
	float distance = distance(cp, texture2D(sampler1, tex_coords).xyz);
	float trans = clamp(pow(distance/c, a), 0, 1);
	gl_FragColor = mix(texture2D(sampler0, tex_coords), color, trans);
}
