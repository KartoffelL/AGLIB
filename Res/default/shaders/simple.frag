#version 330 core

in vec2 tex_coords;

in vec4 fragposition;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;

uniform sampler2D sampler0;

void main() {
	//Texture
	FragColor = texture2D(sampler0, tex_coords);
	if(FragColor.a != 1)
		discard;
	//GBuffer
	FragPos = fragposition;
}
