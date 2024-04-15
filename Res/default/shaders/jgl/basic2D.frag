#version 330 core

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 FragPos;
layout (location = 2) out vec4 FragNormal;
layout (location = 3) out vec4 FragMaterial;

uniform sampler2D sampler;
uniform float radius = 0.05;
uniform float edgeSoftness = 0.00;
uniform vec2 size = vec2(1);
uniform vec2 offset = vec2(0);
uniform vec2 mul = vec2(1);

in vec2 tex_coords;

//from https://iquilezles.org/articles/distfunctions
float roundedBoxSDF(vec2 CenterPosition, vec2 Size, float Radius) {
 return length(max(abs(CenterPosition)-Size+Radius,0.0))-Radius;
}
//https://www.shadertoy.com/view/WtdSDs
float roundB(vec2 fragCoord) {
 float distance 		= roundedBoxSDF(fragCoord.xy-size/2, size/2, radius);
 return 1.0f-smoothstep(0.0f, edgeSoftness * 2.0f,distance);
}

void main() {
	vec2 mappedTexCoords = tex_coords*mul-offset;
	
	FragColor = vec4(1, 1, 1, roundB(mappedTexCoords));
	if(FragColor.a < 0.01)
		discard;
	FragColor *= texture2D(sampler, mappedTexCoords);
	if(FragColor.a < 0.01)
		discard;
	FragPos = vec4(0, 0, 0, 1);
	FragNormal = vec4(0, 0, 0, 1);
	FragMaterial = vec4(0, 0, 0, 1);
}
