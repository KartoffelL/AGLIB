#version 330 core

in vec2 tex_coords;
in vec4 clipspace;

in vec4 fragposition;

layout (location = 0) out vec4 c;
layout (location = 1) out vec4 d;
layout (location = 2) out vec4 e;
layout (location = 3) out vec4 f;
layout (location = 4) out vec4 g;
layout (location = 5) out vec4 a;
layout (location = 6) out vec4 h;
layout (location = 7) out vec4 b;

uniform sampler2D sampler0;
uniform sampler2D sampler1;
uniform sampler2D sampler2;
uniform sampler2D sampler3;
uniform sampler2D sampler4;
uniform sampler2D sampler5;
uniform sampler2D sampler6;
uniform sampler2D sampler7;

uniform float time;
uniform bool ignoreAlpha = true;

void main() {
	//Texture
	vec2 coord = vec2(clipspace.xy/clipspace.w)*.5+.5;
//	vec2 off = vec2(0); //Wobly
//	for(int i = 0; i < 16; i++) {
//		off.x += cos(tex_coords.y*10+i+time*2)*.007;
//		off.y += sin(tex_coords.x*10+i-time*2)*.007;
//	}
//	off *= 1-abs(tex_coords*2-1);
//	coord += off;
	c = texture2D(sampler0, coord);
	if(c.a != 1 && !ignoreAlpha)
		discard;
	d = texture2D(sampler1, coord);
	e = texture2D(sampler2, coord);
	f = texture2D(sampler3, coord);
	g = texture2D(sampler4, coord);
	a = texture2D(sampler5, coord);
	h = texture2D(sampler6, coord);
	b = texture2D(sampler7, coord);
	if(ignoreAlpha) {
		c.a = 1;
		d.a = 1;
		e.a = 1;
		f.a = 1;
		g.a = 1;
		a.a = 1;
		h.a = 1;
		b.a = 1;
	}
}
