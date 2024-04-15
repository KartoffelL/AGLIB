#version 330 core

uniform sampler2D sampler0;

in vec2 tex_coords;

uniform vec4 mask = vec4(1);

void main() {
	gl_FragColor = abs(mask-texture2D(sampler0, tex_coords));
}
