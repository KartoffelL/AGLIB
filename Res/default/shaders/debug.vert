#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;
layout (location = 3) in float material;

uniform mat4 viewMat;
uniform mat4 projectionMat;
uniform mat4 transmat;

uniform vec3 pos1;
uniform vec3 pos2;

uniform bool model;

uniform float zmul = 0.1;

out vec3 normal;

out vec2 tex_coords;
flat out float mat;

void main() {
	vec4 worldposition = vec4(vertices, 1);
	if(model)
		if(worldposition.x == 0)
			worldposition.xyz = pos1;
		else
			worldposition.xyz = pos2;
	gl_Position = projectionMat * viewMat * transmat * worldposition;
	gl_Position.z *= zmul;
	normal = normals;
	tex_coords = textures;
	mat = material;
}
