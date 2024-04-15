#version 330 core

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;
layout (location = 2) out vec4 FragNormal;
layout (location = 3) out vec4 FragMaterial;

uniform sampler2D sampler;

in vec2 tex_coords;

void main() {
	FragColor = texture2D(sampler, tex_coords);
	if(FragColor.a <0.01)
		discard;
	
	FragPos = vec4(0, 0, 0, 1);
	FragNormal = vec4(0, 0, 0, 1);
	FragMaterial = vec4(0, 0, 0, 1);
}
