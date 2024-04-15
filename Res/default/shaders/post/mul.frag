#version 330 core

uniform sampler2D sampler0;
uniform sampler2D sampler1;

in vec2 tex_coords;

void main() {
	gl_FragColor = texture2D(sampler0, tex_coords)*texture2D(sampler1, tex_coords);
}
