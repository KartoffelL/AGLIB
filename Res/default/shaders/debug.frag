#version 330 core


in vec3 normal;
in vec2 tex_coords;
flat in float mat;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;

uniform vec3 color;
uniform vec3 shaded;
uniform sampler2D sampler0;
uniform bool textured;
uniform int materialCount;
uniform bool invert;

void main() {
	//Texture
	FragColor = vec4(color, 1);
	if(textured)
		FragColor = texture2D(sampler0, vec2(((tex_coords.x)/materialCount)+((mat)/materialCount), tex_coords.y));
	if(invert)
		FragColor.rgb = 1-FragColor.rgb;
	if(shaded.x != 0 || shaded.y != 0 || shaded.z != 0)
		FragColor.rgb *= dot(normal, shaded)*.4+.6;
	
}
