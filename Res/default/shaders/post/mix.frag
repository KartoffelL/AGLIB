#version 330 core

uniform sampler2D sampler0;
uniform sampler2D sampler1;

in vec2 tex_coords;

uniform vec4 a = vec4(1);
uniform vec4 b = vec4(1);
uniform bool blend;

void main() {
	gl_FragColor = texture2D(sampler0, tex_coords)*a+texture2D(sampler1, tex_coords)*b;
	if(blend) {
		vec4 aa = texture2D(sampler0, tex_coords)*a;
		vec4 bb = texture2D(sampler1, tex_coords)*b;
		gl_FragColor = vec4(aa.rgb*aa.a+bb.rgb, 1-(1-aa.a)*bb.a);
	}
}
