#version 330 core

layout (location = 0) out vec4 c1;

uniform sampler2D sampler0;
uniform sampler2D sampler1;
uniform sampler2D sampler2;
uniform sampler2D sampler3;
uniform sampler2D sampler4;

uniform vec4 pos1;
uniform vec4 pos2;
uniform vec4 pos3;
uniform vec4 pos4;

in vec2 tex_coords;

void main() {
	c1 = texture2D(sampler0, tex_coords);
	
	vec2 p = tex_coords*pos1.zw+pos1.xy;
	if(p.x < 1 && p.x > 0 && p.y < 1 && p.y > 0)
		c1 = texture2D(sampler1, p);
	p = tex_coords*pos2.zw+pos2.xy;
	if(p.x < 1 && p.x > 0 && p.y < 1 && p.y > 0)
		c1 = texture2D(sampler2, p);
	p = tex_coords*pos3.zw+pos3.xy;
	if(p.x < 1 && p.x > 0 && p.y < 1 && p.y > 0)
		c1 = texture2D(sampler3, p);
	p = tex_coords*pos4.zw+pos4.xy;
	if(p.x < 1 && p.x > 0 && p.y < 1 && p.y > 0)
		c1 = texture2D(sampler4, p);
}
