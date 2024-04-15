#version 330 core

uniform sampler2D sampler0;
uniform sampler2D sampler1; //Depth

in vec2 tex_coords;


uniform bool bokeh = true;
uniform float Directions = 32.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
uniform float Quality = 6.0; // BLUR QUALITY (Default 4.0 - More is better but slower)
uniform float Size = 32.0; // BLUR SIZE (Radius)


const int rep = 5;
const float repS = 0.447;
uniform float weight[5] = float[] (0.2, 0.15, 0.11, 0.09, 0.05);
uniform vec2 tex_offset = vec2(0.001);
uniform float alphalock = 0;

const float pi = 3.14159265359;

void main() {
	if(!bokeh) {
		vec4 col = vec4(0);
		for(int x = -rep; x <= rep; x++) {
			vec2 tex = vec2(tex_coords.x+x*tex_offset.x, tex_coords.y);
			col += texture2D(sampler0, clamp(tex, 1, 0))*weight[abs(x)];
		}
		for(int y = -rep; y <= rep; y++) {
			vec2 tex = vec2(tex_coords.x, tex_coords.y+y*tex_offset.y);
			col += texture2D(sampler0, clamp(tex, 1, 0))*weight[abs(y)];
		}
		for(int x = -rep; x <= rep; x++) {
			vec2 tex = vec2(tex_coords.x+x*tex_offset.x*repS, tex_coords.y+x*tex_offset.y*repS);
			col += texture2D(sampler0, clamp(tex, 1, 0))*weight[abs(x)];
		}
		for(int y = -rep; y <= rep; y++) {
			vec2 tex = vec2(tex_coords.x+y*tex_offset.x*repS, tex_coords.y-y*tex_offset.y*repS);
			col += texture2D(sampler0, clamp(tex, 1, 0))*weight[abs(y)];
		}
		gl_FragColor = col/4;
		if(alphalock != 0)
			gl_FragColor.a = alphalock;
	}
	else {
		vec4 col = vec4(0);
		for( float d=0.0; d<pi*2; d+=pi/Directions)
	    {
			for(float i=1.0/Quality; i<=1.0; i+=1.0/Quality)
	        {
				col += texture( sampler0, tex_coords+vec2(cos(d),sin(d))*Size*i*vec2(tex_offset));		
	        }
	    }
		gl_FragColor = col/(Quality * Directions);
		if(alphalock != 0)
			gl_FragColor.a = alphalock;
	}
}
