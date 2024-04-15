#version 330 core

layout (location = 0) out vec4 c1;
//layout (location = 1) out vec4 c2;
//layout (location = 2) out vec4 c3;
//layout (location = 3) out vec4 c4;
//layout (location = 4) out vec4 c5;
//layout (location = 5) out vec4 c6;
//layout (location = 6) out vec4 c7;
//layout (location = 7) out vec4 c8;

uniform sampler2D sampler0;
//uniform sampler2D sampler1;
//uniform sampler2D sampler2;
//uniform sampler2D sampler3;
//uniform sampler2D sampler4;
//uniform sampler2D sampler5;
//uniform sampler2D sampler6;
//uniform sampler2D sampler7;

uniform float alphalock = 0;

in vec2 tex_coords;

void main() {
	c1 = texture2D(sampler0, tex_coords);
	if(alphalock != 0) {
		c1.a = alphalock;
	}
//	c2 = texture2D(sampler1, tex_coords);
//	c3 = texture2D(sampler2, tex_coords);
//	c4 = texture2D(sampler3, tex_coords);
//	c5 = texture2D(sampler4, tex_coords);
//	c6 = texture2D(sampler5, tex_coords);
//	c7 = texture2D(sampler6, tex_coords);
//	c8 = texture2D(sampler7, tex_coords);
}
