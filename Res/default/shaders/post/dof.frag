#version 330 core

uniform sampler2D sampler0; //Color
uniform sampler2D sampler1; //Color 2
uniform sampler2D sampler2; //Position

in vec2 tex_coords;

uniform vec2 clip;
uniform mat4 vm;
uniform vec2 mp;

//float linearize_depth(float d)
//{
//    float n = 2*d-1;
//    return 2 * clip.x * clip.y / (clip.y + clip.x - d * (clip.y - clip.x));
//}

float d(vec4 v) {
	return v.z/v.w;
}

void main() {
	float depth = d(vm * vec4(texture2D(sampler2, tex_coords).xyz, 1));
//	float c = d(vm * vec4(texture2D(sampler2, mp).xyz, 1));
	float c = 0;
	float k = 50;
	float dm = min(abs((depth-c)/k), 1); //pow(1-pow(1/((depth-c)*(depth-c)+1), a), k)
	gl_FragColor = mix(texture2D(sampler0, tex_coords), texture2D(sampler1, tex_coords), dm); //WIP
}
