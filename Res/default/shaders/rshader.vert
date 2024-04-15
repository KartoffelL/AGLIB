#version 330 core

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normals;
layout (location = 3) in float material;

uniform mat4 viewMat;
uniform mat4 viewMatInv;
uniform mat4 projectionMat;

out mat4 vmi;
out mat4 pmi;
out mat4 pm;
out mat4 vm;

out vec3 cp;

out vec2 tex_coords;

void main() {
	gl_Position = vec4(vertices, 1);

	vmi = viewMatInv;
	pmi = inverse(projectionMat);
	pm = projectionMat;
	vm = viewMat;

	cp = (viewMatInv * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
	tex_coords = textures;

	tex_coords = tex_coords - 0.5;
	tex_coords.y = -tex_coords.y;

}
